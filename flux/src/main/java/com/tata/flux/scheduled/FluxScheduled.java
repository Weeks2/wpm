package com.tata.flux.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class FluxScheduled {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final WebClient webClient;

  public FluxScheduled(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("http://localhost:8080/flux").build();
  }

  //@Scheduled(cron = "0 * * * * ?")
  public void executeTask() {
    log.info("Every minute");
    //scheduler.schedule(this::start, 0, TimeUnit.MILLISECONDS);
  }

  private void start()
  {
      AtomicLong processedCount = new AtomicLong();
      DataSetRequest request = new DataSetRequest(1,"40");
      webClient.post().uri("/stream")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .header(HttpHeaders.ACCEPT_ENCODING, "gzip")
              .accept(MediaType.APPLICATION_STREAM_JSON)
              .body(BodyInserters.fromObject(request))
              .retrieve()
              .bodyToFlux(FluxDataRecord.class)
              .doOnNext(response -> {
                processedCount.getAndIncrement();
              })
              .doOnTerminate( () -> log.info(processedCount.toString()))
              .subscribe(data -> log.info(data.toString()));;
      log.info("request ..");

  }
}

