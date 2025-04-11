package org.example.sjznpeswebdemo.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
public class CrawlerService {

    Flux<LocalDate> dateProducer(LocalDate start) {
        Flux<LocalDate> flux = Flux.create(fluxSink -> {
            // LocalDate start = LocalDate.parse("2025-02-01");
            LocalDate end = LocalDate.now();

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                fluxSink.next(d);
            }

            fluxSink.complete();
        });
        return flux;
    }

}
