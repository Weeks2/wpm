package com.tata.flux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class TestWebflux {
    @Test
    void testFlow() {

        Flux.just("data")
                .switchIfEmpty(i->Flux.just(""))
                .flatMap(data-> {
                  return null;
                }).subscribe();

        Flux.just().collectList()
                .doOnNext(data -> {
                    if (data.isEmpty()) {
                        System.out.println("no data found v1");
                    }
                })
                .filter(data-> !data.isEmpty())
                .flatMap(bool -> {
                    System.out.println("publishing v1");
                    return null;
                }).subscribe();

        Flux.just().collectList()
                .filter(data-> !data.isEmpty())
                .switchIfEmpty(Mono.fromRunnable(() -> { System.out.println("no data found v2");}))
                .flatMap(data -> {
                    System.out.println("publishing v2");
                    return Mono.empty();
                }).subscribe();
    }
}
