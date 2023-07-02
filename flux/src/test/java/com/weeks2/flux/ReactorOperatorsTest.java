package com.weeks2.flux;

import com.tata.flux.service.FileWriterUtility;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.Stopwatch;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.util.StopWatch;
import reactor.core.publisher.Sinks;


@ExtendWith(MockitoExtension.class)
public class ReactorOperatorsTest {


    private StopWatch stopWatch;


    @BeforeEach
    public void setUp() {
        stopWatch = new StopWatch();
        stopWatch.start();
    }


    @AfterEach
    public void tearDown() {
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis() + " ms");
        System.out.println(stopWatch.getTotalTimeSeconds() + " seconds");
    }


    @Test
    void fluxTest()
    {
        Flux<Integer> numbers = Flux.range(1, 5);
        // Operator map: Multiply each number by 2
        Flux<Integer> mapped = numbers.map(number -> number * 2);
        // Operator flatMap: Generate a stream of strings for each number
        Flux<String> flatMapped = numbers.flatMap(number -> Flux.just("Number: " + number));
        // Operator concatMap: Generate a Mono that emits the number concatenated with "!"
        Flux<String> concatMapped = numbers.concatMap(number -> Mono.just(number + "!"));
        // Operator flatMapMany: Generate a stream of integers for each number
        Flux<Integer> flatMapMany = Mono.just(1).flatMapMany(number -> Flux.range(1, number).map(n -> n * 10));
        // Multiply each number in the range by 10
        mapped.subscribe(System.out::println);
        flatMapped.subscribe(System.out::println);
        concatMapped.subscribe(System.out::println);
        flatMapMany.subscribe(System.out::println);
    }

    @Test
    void sink(){
        Flux<Integer> numbers = Flux.range(1, 3);
        numbers.handle((number, sink) -> {
                    if (number % 2 == 0) {
                        sink.next(number * 10); // emit element
                    } else {
                        // ignore element
                    }
                })
                .subscribe(System.out::println);
    }

    @Test
    void hasElements() {
        Flux<String> flux = Flux.range(1, 2000_000_000).map(i->  ".".repeat(1000));
        flux.take(1).hasElements().subscribe(hasElements -> {
                    if (hasElements) {
                        System.out.println("El Flux tiene al menos un elemento.");
                    } else {
                        System.out.println("El Flux está vacío.");
                    }
                });
        flux.take(1).concatWith(flux.skip(1)).subscribe(System.out::println);
        flux.take(1).hasElements().filter(hasElements -> hasElements).flatMapMany(bool->{
            if(bool.booleanValue()){
                return flux.map(item->item);
            }
            return flux;
        });
    }
    @Test
    void test(){
        int amount = 9_000_000;
        FileWriterUtility fileWriterUtility = new FileWriterUtility();
        fileWriterUtility.build(Flux.just("a","b","c"),"header","fileName",".dat").subscribe();
    }
    @Test
    void hasElement() {
        Flux<String> flux = Flux.range(0, 50_000_000).map(i->  i+"1".repeat(256/6));
        FileWriterUtility fileWriterUtility = new FileWriterUtility();

        flux.take(1).hasElements().filter(hasElements -> hasElements)
                .flatMapMany(__-> fileWriterUtility.build(flux,"header_flux","fileName_flux",".dat")
        ).subscribe();
    }

    @Test
    void testSink(){
        Flux<Integer> numberFlux = Flux.range(1, 5);  // Create a stream of numbers
        // Create two sinks using Sinks.Many
        Sinks.Many<String> pairSink = Sinks.many().unicast().onBackpressureBuffer();
        Sinks.Many<String> oddSink = Sinks.many().unicast().onBackpressureBuffer();
        // Send elements to the streams depending on whether it is even or odd
        numberFlux.handle((numberItem, sink) -> {
            if (numberItem % 2 == 0) {
                pairSink.emitNext("Pair: " + numberItem, Sinks.EmitFailureHandler.FAIL_FAST); // Cast element to peer stream
            } else {
                oddSink.emitNext("Odd: " + numberItem, Sinks.EmitFailureHandler.FAIL_FAST); // Cast element to odd stream
            }
            if (numberItem == 5) {
                pairSink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST); // End the even stream
                oddSink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST); // End the odd stream
            }
        }).subscribe();
        // Subscribe to the streams and process the elements
        Flux<String> pairFlux = pairSink.asFlux();
        Flux<String> oddFlux = oddSink.asFlux();
        pairFlux.subscribe(pairNumber -> {System.out.println("Peer item received: " + pairNumber);});
        oddFlux.subscribe(oddNumber -> {System.out.println("Odd item received: " + oddNumber);});
    }

    @Test
    void merge() {
        Flux<Integer> numberFlux = Flux.range(1, 5); // Crear un flujo de números

        Flux<String> pairFlux = numberFlux.filter(number -> number % 2 == 0) // Filtrar números pares
                .map(number -> "Pair: " + number); // Mapear a cadena de texto con "Pair: "

        Flux<Integer> oddFlux = numberFlux.filter(number -> number % 2 != 0) // Filtrar números impares
                .map(number -> number); // Mapear a cadena de texto con "Odd: "

        Flux<Object> mergedFlux = Flux.merge(pairFlux, oddFlux); // Combinar los flujos utilizando merge()

        mergedFlux.subscribe(item -> {
            System.out.println("Item received: " + item);
        });
    }

    @Data
    @Builder
    class RowItem {
        private int id;
        private String title;
        private String errors;
    }

    @Test
    void testSink2(){
        Flux<Integer> numberFlux = Flux.just(1,2,3,4,5);
        Sinks.Many<RowItem> pairSink = Sinks.many().unicast().onBackpressureBuffer();
        numberFlux.handle((numberItem, sink) -> {
            if (numberItem % 2 == 0) {
                pairSink.emitNext(RowItem.builder().build(), Sinks.EmitFailureHandler.FAIL_FAST); // Cast element to peer stream
            }
            sink.next(numberItem);
        }).doOnNext(i-> {
            System.out.println(i);
        }).subscribe();

        pairSink.asFlux().subscribe(pairNumber -> {
            System.out.println("Peer item received: " + pairNumber.getTitle());
        });
    }
}
