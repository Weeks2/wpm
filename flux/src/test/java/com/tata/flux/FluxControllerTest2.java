package com.tata.flux;

import com.tata.flux.controller.FluxController;
import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import com.tata.flux.service.FluxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FluxControllerTest2 {

    WebTestClient webTestClient;
    FluxController exampleController;
    @Mock
    FluxService exampleService;

    @BeforeEach
    void setUp() {
        exampleController = new FluxController(exampleService);
        webTestClient = WebTestClient
                .bindToController(exampleController)
                .configureClient()
                .build();
    }

    @Test
    public void testGetAllItems() {
        Flux<FluxDataRecord> items = getAllRecords();
        when(exampleService.getAllRecords(any(DataSetRequest.class))).thenReturn(items);

        webTestClient.get().uri("/records")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(String.class)
                .hasSize(3)
                .consumeWith(response -> {
                    List<String> responseItems = response.getResponseBody();
                    for (int i = 0; i < responseItems.size(); i++) {
                  //      assertEquals(items.get(i), responseItems.get(i));
                    }
                });
    }

    @Test
    public void testGetItemById() {
       // when(exampleService.getItemById(any())).thenReturn(Mono.just("item1"));

        webTestClient.get().uri("/items/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .consumeWith(response -> {
                    String item = response.getResponseBody();
                    assertEquals("item1", item);
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

