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

    @PostConstruct
    void init() {
        log.info("....");
        header("1").map(totalPages->{
            log.info("page {}",totalPages);
            load("1");
            return Mono.empty();
        }).subscribe();

    }
    public Mono<Integer> header(String siteId) {
        return webClient.get().uri(createUri(siteId))
                .exchangeToMono(response -> response.toBodilessEntity())
                .map(entity -> entity.getHeaders().getFirst("x-wp-total"))
                .map(totalPagesStr -> totalPagesStr != null ? Integer.parseInt(totalPagesStr) : 0);
    }

    private void load(String siteId) {
        webClient.get().uri(createUri(siteId)).retrieve().bodyToFlux(Post.class)
                .doOnNext(d-> {
                    log.info("{}",d.getTitle());
                }).subscribe();
    }
    public Flux<Post> loadData(String siteId) {
        header(siteId).map(totalPages->{
           log.info("page {}",totalPages);
            return Mono.empty();
          });
        return Flux.empty();
    }
    public Flux<Post> loadData(String site, int totalPages) {
        Flux.range(1, totalPages).concatMap(i -> {
            return webClient.get()
                    .uri(createUri(site))
                    .retrieve()
                    .bodyToFlux(Post.class)
                    .doOnNext(d-> {
                        log.info("{}",d.getTitle());
                    });
        }).doOnComplete(() -> {
        });
       return Flux.just();
    }

    private String createUri(String uriId) {
        return SitiosWeb.getLetter(uriId) + WP_API_;
    }
}
