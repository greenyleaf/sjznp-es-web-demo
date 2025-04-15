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

    @Test
    void test4() {
        Flux.range(2, 10 - 1)
                .doOnNext(integer -> {
                    log.info("doOnNext, {}", integer);
                })
                .blockLast()
        ;
    }

    @Test
    void test5() {
        Flux.just(3, 5, 7)
                .doOnNext(integer -> {
                    log.info("integer 1, {}", integer);
                })
                .delayElements(Duration.ofSeconds(3))
                .doOnNext(integer -> {
                    log.info("integer 2, {}", integer);
                })
                .blockLast();
    }

    @Test
    void test6() {
        Mono.just(5)
                .doOnNext(integer -> {
                    log.info("integer 1, {}", integer);
                })
                .delayElement(Duration.ofSeconds(3))
                .doOnNext(integer -> {
                    log.info("integer 2, {}", integer);
                })
                .block();
    }

    @Test
    void test7() {
        Mono.empty()
                .doOnNext(integer -> {
                    log.info("integer 1, {}", integer);
                })
                .then(Mono.just(5))
                .doOnNext(integer -> {
                    log.info("integer 2, {}", integer);
                })
                .block();
    }

    @Test
    void test8() {
        Mono.empty()
                .doOnNext(integer -> {
                    log.info("integer 1, {}", integer);
                })
                .thenReturn(5)
                .doOnNext(integer -> {
                    log.info("integer 2, {}", integer);
                })
                .block();
    }

    @Test
    void test9() {
        Flux.range(1, 10)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(integer -> {
                    log.info("integer, {}", integer);
                })
                .concatMap(integer -> {
                    // .flatMap(integer -> {
                    // .flatMapSequential(integer -> {
                    // return Mono.just(integer).delayElement(Duration.ofMillis(1200 - integer * 100));
                    return Mono.just(integer).delayElement(Duration.ofMillis(400 * 11 - integer * 400));
                })
                .doOnNext(integer -> {
                    log.info("integer 2, {}", integer);
                })
                .blockLast();
    }

    @Test
    void test10() {
        Flux.empty()
                .count()
                .doOnNext(integer -> {
                    log.info("integer, {}", integer);
                })
                .block();
    }

    @Test
    void test11() {
        Flux.defer(() -> {
                    return
                            Flux.create(fluxSink -> {
                                for (int i = 0; i < 10; i++) {
                                    log.info("stage 1");
                                    fluxSink.next(i);
                                }
                                fluxSink.complete();
                            });
                })
                .delayElements(Duration.ofMillis(100))
                .doOnNext(integer -> {
                    log.info("integer, {}", integer);
                })
                .blockLast();
    }
}
