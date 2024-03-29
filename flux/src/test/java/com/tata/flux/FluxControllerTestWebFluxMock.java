package com.tata.flux;

import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import com.tata.flux.service.FluxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
//@WebFluxTest(controllers = FluxController.class)
//@Import(FluxService.class)
public class FluxControllerTestWebFluxMock {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FluxService fluxService;

    @Test
    public void testGetDataEndpoint() {
        FluxService fluxService = new FluxService();
        DataSetRequest request = new DataSetRequest(1, "item1");
        FluxDataRecord record1 = new FluxDataRecord(1, "item0");
        FluxDataRecord record2 = new FluxDataRecord(2, "item1");
        List<FluxDataRecord> records = Arrays.asList(record1, record2);
        Mockito.when(fluxService.getAllRecords(any(DataSetRequest.class))).thenReturn(Flux.fromIterable(records));

        webTestClient.post().uri("https://localhost:8080/flux/records")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), DataSetRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(FluxDataRecord.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<FluxDataRecord> data = response.getResponseBody();
                    assertEquals("item0", data.get(0).getName());
                    assertEquals("item1", data.get(1).getName());
                });
    }

}
