package com.tata.flux.wpms;

import com.tata.flux.service.FtpUtility;
import com.tata.flux.service.SitesService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<String> pullSitesDefault() {
        return SitesService.getAllSitesInfo().flatMap(siteInfo->{
                    try {
                        var data = pullAllDataBy(siteInfo.getBaseUri()).map(this::convertTitle);
                        return ftpUtility.build(data, "site_"+siteInfo.getId(),"header",".txt");
                    }
                    catch (Exception e) {
                        return Flux.error(e);
                    }
                },6)
                .onErrorContinue((t,o) -> {
                    log.error("{}", t.getMessage());
                });
    }

    private Flux<Post> pullAllDataBy(String baseUri) {
        return readHeader(SitesService.buildUri(baseUri,100,1)).flatMapMany(totalPages->
                Flux.range(1,totalPages)
                        .concatMap(page-> requestByUri(SitesService.buildUri(baseUri,100,page)))
        );
    }
    private Flux<Post> pullByMaxPage(String baseUri, int perPage,int maxPages) {
        return Flux.range(1,maxPages).concatMap(page-> requestByUri(SitesService.buildUri(baseUri,100,page)));
    }
    private String convertTitle(Post post)
    {
        return post.getTitle().getRendered();
    }

    private Flux<Post> pullLastPage(String baseUri) {
        return requestByUri(SitesService.buildUri(baseUri,100,1));
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
    private Mono<Integer> readHeader(String uri) {
        if(!uri.contains("page")) log.info("check uri {}",uri);
        return webClient.get().uri(uri).exchangeToMono(response -> response.toBodilessEntity())
                .map(entity -> entity.getHeaders().getFirst("x-wp-totalpages"))
                .map(totalPagesStr -> totalPagesStr != null ? Integer.parseInt(totalPagesStr) : 0);
    }
}
