package com.tata.flux.controller;

import com.tata.flux.model.DataSetRequest;
import com.tata.flux.model.FluxDataRecord;
import com.tata.flux.service.FluxService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Data
@RestController
@RequestMapping("/flux")
@AllArgsConstructor
public class FluxController {

    private final FluxService fluxService;

    @PostMapping(value = "/stream", produces = { "application/json", "application/stream+json" })
    public ResponseEntity<Flux<FluxDataRecord>> getData(@RequestBody DataSetRequest request) {
        log.info(request.toString());
        return ResponseEntity.ok()
            //    .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .body(fluxService.getAllRecords(request));
    }

    @PostMapping (value = "/records",produces = { "application/json", "application/stream+json" })
    public  Flux<FluxDataRecord> getDataJson(@RequestBody DataSetRequest request) {
        log.info(request.toString());
        return fluxService.getAllRecords(request);
    }

    @PostMapping (value = "/import")
    public Mono<String> getDataJsonGet(@RequestBody DataSetRequest request) {
        log.info(request.toString());
        return Mono.just("getDataJsonGet");
    }
}

