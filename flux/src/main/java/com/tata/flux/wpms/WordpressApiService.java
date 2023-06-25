package com.tata.flux.wpms;

import com.tata.flux.model.SitiosWeb;
import com.tata.flux.service.FtpUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Data
@Service
public class WordpressApiService {
    private final WebClient webClient;
    private final FtpUtility ftpUtility;

    public WordpressApiService(WebClient.Builder webClientBuilder, FtpUtility ftpUtility) {
        this.webClient = webClientBuilder.build();
        this.ftpUtility = ftpUtility;
    }

    public Flux<String> triggerPullWpData() {
        return Flux.fromStream(Arrays.stream(SitiosWeb.values())).flatMap(site->{
                    try {
                        Flux<String> data = pullDataByNumberOfSite(site.getNumber()).map(this::convertTitle);
                        return ftpUtility.build(data,"site_complete"+site.getNumber(),"header",".txt");
                    }
                    catch (Exception e) {
                        return Flux.error(e);
                    }
                },5)
                .onErrorContinue((t,o) -> {
                    log.error("{}", t.getMessage());
                });
    }

    public Flux<String> triggerPullWpDataLast() {
        return Flux.fromStream(Arrays.stream(SitiosWeb.values())).flatMap(site->{
                    try {
                        Flux<String> data = pullLastPage(site.getNumber()).map(this::convertTitle);
                        return ftpUtility.build(data,"last100_site_"+site.getNumber(),"header",".txt");
                    }
                    catch (Exception e) {
                        return Flux.error(e);
                    }
                },5)
                .onErrorContinue((t,o) -> {
                    log.error("{}", t.getMessage());
                });
    }

    private String convertTitle(Post post)
    {
        return post.getTitle().getRendered();
    }

    private Flux<Post> pullLastPage(String number) {
        return requestByUri(SitiosWeb.getBaseLink(number).replace("PAGE", "1"));
    }

    private Flux<Post> pullDataByNumberOfSite(String number) {
        Flux<Post> posts = readHeader("x-wp-totalpages",SitiosWeb.getLetter(number))
                .flatMapMany(totalPages-> Flux.range(1,totalPages).concatMap(page->
                                requestByUri(SitiosWeb.getBaseLink(number).replace("PAGE", page.toString()))
                        )
                );
        return posts;
    }

    private Flux<Post> requestByUri(String uri) {
        log.info("site {}",uri);
        return webClient.get().uri(uri).retrieve()
                .bodyToFlux(Post.class)
                .onErrorResume(error -> {
                    if (error instanceof WebClientRequestException) {
                        log.info("exception: {}", error.getMessage());
                    };
                    return Mono.empty();
                })
                .doOnNext(d-> log.info("{}",d.getTitle()));
    }

    public Mono<Integer> readHeader(String header, String uri) {
        return webClient.get().uri(uri).exchangeToMono(response -> response.toBodilessEntity())
                .map(entity -> entity.getHeaders().getFirst(header))
                .map(totalPagesStr -> totalPagesStr != null ? Integer.parseInt(totalPagesStr) : 0);
    }

}
