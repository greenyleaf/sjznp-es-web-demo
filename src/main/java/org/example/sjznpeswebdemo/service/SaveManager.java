package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.repository.PriceItemRepository;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SaveManager {
    private final PricePageRepository pricePageRepository;
    private final PriceItemRepository priceItemRepository;

    public SaveManager(PricePageRepository pricePageRepository, PriceItemRepository priceItemRepository) {
        this.pricePageRepository = pricePageRepository;
        this.priceItemRepository = priceItemRepository;
    }

    public Mono<Long> save(Flux<PricePage> pricePageFlux, Flux<PriceItem> priceItemFlux) {
        log.info("save entered");

        try {
            return pricePageRepository
                    .saveAll(pricePageFlux)
                    .doOnNext(pricePage -> log.info("stage 1"))
                    .then(
                            priceItemRepository.saveAll(priceItemFlux)
                                    .count()
                                    .doOnNext(count -> log.info("stage 4, count: {}", count))
                    )
                    .doOnNext(count -> log.info("stage 2, count: {}", count))
                    ;
        } finally {
            log.info("save left");
        }
    }
}
