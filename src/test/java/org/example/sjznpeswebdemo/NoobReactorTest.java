package org.example.sjznpeswebdemo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Disabled
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
}
