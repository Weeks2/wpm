package com.tata.flux;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = FluxController.class)
@Import(FluxService.class)
public class FluxControllerTestWebFlux {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testWebTestClient() {
        DataSetRequest request = new DataSetRequest(1, "item1");
        webTestClient.post().uri("https://localhost:8080/flux/records")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), DataSetRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FluxDataRecord.class)
                .hasSize(10)
                .consumeWith(response -> {
                    List<FluxDataRecord> data = response.getResponseBody();
                    assertEquals("item0", data.get(0).getName());
                });
    }

    @Test
    public void testWebClient() {

        WebClient client = WebClient.create("http://localhost:8080/flux");
        Mono<String> response = client.post()
                .uri("/import")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(new DataSetRequest(1,"item1")))
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(response)
                .expectNext("getDataJsonGet")
                .verifyComplete();
    }
}
