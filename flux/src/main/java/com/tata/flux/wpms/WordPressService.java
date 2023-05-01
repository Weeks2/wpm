package com.tata.flux.wpms;

import com.tata.flux.model.SitiosWeb;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Service
public class WordPressService {
    private final WebClient webClient;

    @Value("${wordpress.api_page}")
    private  String WP_API_;
    public WordPressService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public Flux<Post> getPostsBy(PostRequest postRequest) {
        return getPosts(postRequest.getBaseUrl()).take(postRequest.getCount());
    }
    public Flux<Post> getPosts(String site) {
        return webClient.mutate().baseUrl(createUri(site)).build()
                .get()
                .uri(createUri(site))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Post.class)
                .doOnNext(d-> {
                    log.info("datos {}",d.getTitle());
                })
                .onErrorResume(throwable -> {
                    if (site.isBlank()) {
                        log.error("La URL de la base está en blanco");
                    }
                    log.error("Se ha producido un error al obtener los datos: {}", createUri(site));
                    return Flux.empty();
                });
    }

    public Flux<Post> loadData(String site, int totalPages) {
        return Flux.range(1, totalPages).concatMap(i -> {
            return webClient.get()
                    .uri(createUri(site))
                    .retrieve()
                    .bodyToFlux(Post.class)
                    .doOnNext(d-> {
                        log.info("{}",d.getTitle());
                    });
        }).doOnComplete(() -> {
        });
    }

    private String createUri(String uriId) {
        return SitiosWeb.getLetter(uriId) + WP_API_;
    }


    void init() {
        header("1").map(totalPages->{
          Flux.range(1,totalPages).concatMap(page-> {
                return load("1",page.toString());
            }).subscribe();
            return Mono.empty();
        }).subscribe();
    }

    @PostConstruct
    void initPages() {
        Flux.fromStream(Arrays.stream(SitiosWeb.values())).concatMap(site->{
            String uri = site.getLetter() +WP_API_ .replace("i","1");
            log.info("site {}",uri);
            return Flux.empty();
        }).subscribe();
    }

    /**
     * X-WP-Total: 17454
     * X-WP-TotalPages: 175
     */
    public Mono<Integer> header(String siteId) {
        String uri = createUri(siteId);
        log.info(uri);
        return webClient.get().uri(uri).exchangeToMono(response -> response.toBodilessEntity())
                .map(entity -> entity.getHeaders().getFirst("x-wp-totalpages"))
                .map(totalPagesStr -> totalPagesStr != null ? Integer.parseInt(totalPagesStr) : 0);
    }

    private Flux<Post> load(String siteId,String i) {
        String uri = createUri(siteId).replace("i",i);
        return webClient.get().uri(uri)
                .retrieve().bodyToFlux(Post.class)
                .doOnNext(d-> {
                    //log.info("{}",d.getTitle());
                });
    }
}
