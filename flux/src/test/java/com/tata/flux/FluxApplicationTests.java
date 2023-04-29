package com.tata.flux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class FluxApplicationTests {

	private  WebTestClient webTestClient;

	FluxController fluxController;
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
	public void testGetDataEndpoint() {
		DataSetRequest request = new DataSetRequest(1, "item1");
		webTestClient.post().uri("/records")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), DataSetRequest.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
				.expectBodyList(FluxDataRecord.class)
				.hasSize(10)
				.consumeWith(response -> {
					List<FluxDataRecord> data = response.getResponseBody();
					assertEquals("item1", data.get(0).getName());
				});
	}

	@Test
	public void test1()
	{
		webTestClient.get().uri("callicoder.com")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void testWebFlux() {
		DataSetRequest request = new DataSetRequest(1,"item1");
		webTestClient.post().uri("/records")
				.body(request, DataSetRequest.class)
				.exchange()
				.expectStatus().isOk()
			    .expectBody(FluxDataRecord.class)
				.consumeWith(response -> {
						FluxDataRecord data = response.getResponseBody();
						assertEquals("item1", data.getName());
						assertEquals(1, data.getId());
		      });
	}

}
