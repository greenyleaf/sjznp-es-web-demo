package org.example.sjznpeswebdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Disabled
@Slf4j
public class NoobReactorTest {
    @Test
    void test1() throws InterruptedException {
        Flux<Integer> flux = Flux
                .range(1, 1000)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> System.out.println("canceled"))
                .doOnNext(System.out::println);

        Disposable disposable = flux.subscribe();
        disposable.dispose();

//        TimeUnit.SECONDS.sleep(3);

        flux.blockLast();
    }

    @Test
    void test2() {
        Mono<Integer> mono = Mono.just(1);

        mono.subscribe(i -> {
            log.info("subscribe, {}", i);
        });

        mono.doOnNext(i -> {
                    log.info("doOnNext 1, {}", i);
                })
                .block();

        mono.doOnNext(i -> {
                    log.info("doOnNext 2, {}", i);
                })
                .block();
    }

    @Test
    void test3() {
        Mono.just(1)
                .doOnNext(integer -> {
                    log.info("doOnNext 1, {}", integer);
                })
                .then()
                .thenReturn(1)
                .doOnNext(integer -> {
                    log.info("doOnNext 2");
                })
                .block()
        ;
    }
}
