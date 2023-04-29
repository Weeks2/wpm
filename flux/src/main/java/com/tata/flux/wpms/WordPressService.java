package com.tata.flux.wpms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class WordPressService {
    private final WebClient webClient;
    @Value("${wordpress.api_with_params}")
    private String WP_API_WITH_PARAMS;

    public WordPressService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public Flux<Post> getPostsBy(PostRequest postRequest) {
        return getPosts(postRequest.getBaseUrl()).take(postRequest.getCount());
    }
    public Flux<Post> getPosts(String baseUrl) {
        log.info(baseUrl + WP_API_WITH_PARAMS);
        return webClient.mutate().baseUrl(baseUrl).build()
                .get()
                .uri(baseUrl + WP_API_WITH_PARAMS)
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
