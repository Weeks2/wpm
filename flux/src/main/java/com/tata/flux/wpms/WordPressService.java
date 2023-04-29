package com.tata.flux.wpms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class WordPressService {
    private final WebClient webClient;
    private String baseUrl = "";

    public WordPressService(WebClient.Builder webClientBuilder) {
        log.info("WordPressService");
        this.webClient = webClientBuilder.build();
    }

    public Flux<Post> getPosts(String baseUrl) {
        this.baseUrl = baseUrl;
        return request(this.baseUrl);
    }
    public Flux<Post> getPosts(PostRequest postRequest) {
        return request(postRequest.getBaseUrl()).take(postRequest.getCount());
    }
    public Flux<Post> getPosts() {
        return request(this.baseUrl);
    }

    private Flux<Post> request2(String baseUrl)
    {
        log.info(baseUrl);
        return baseUrl.isBlank() ? Flux.just() :
                webClient.mutate().baseUrl(baseUrl).build().get()
                .uri("/wp-json/wp/v2/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Post.class)
                        .doOnError(red-> {
                    log.info("error {}",red);
                        });
    }

    private Flux<Post> request(String baseUrl) {
        log.info(baseUrl);
        return webClient.mutate().baseUrl(baseUrl).build().get()
                .uri("/wp-json/wp/v2/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Post.class)
                .doOnNext(d-> {
                    log.info("datos {}",d.getTitle());
                })
                .onErrorResume(throwable -> {
                    if (baseUrl.isBlank()) {
                        log.error("La URL de la base está en blanco");
                    }
                    log.error("Se ha producido un error al obtener los datos: {}", baseUrl);
                    return Flux.empty();
                });
    }

}
