package com.tata.flux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FluxControllerTest {

    WebTestClient webTestClient;

    FluxController fluxController;
    @Mock
    FluxService service;

    @BeforeEach
    void setUp() {
        fluxController = new FluxController(service);
        webTestClient = WebTestClient
                .bindToController(fluxController)
                .configureClient()
                .build();
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
    @Test
    public void testWebTestClient()
    {
        DataSetRequest request = new DataSetRequest(1, "item1");
        when(service.getAllRecords(any(DataSetRequest.class))).thenReturn(getAllRecords());
        webTestClient.post().uri("/records")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(request))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FluxDataRecord.class)
                .hasSize(10)
                .consumeWith(response -> {
                    List<FluxDataRecord> data = response.getResponseBody();
                    assertEquals("item1", data.get(0).getName());
                });
    }

    public Flux<FluxDataRecord> getAllRecords()
    {
        List<FluxDataRecord> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new FluxDataRecord(i,"item" + i));
        }
        return Flux.fromIterable(list);
    }
}
