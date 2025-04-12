package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.repository.PriceItemRepository;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class SaveManager {
    private final PricePageRepository pricePageRepository;
    private final PriceItemRepository priceItemRepository;

    public SaveManager(PricePageRepository pricePageRepository, PriceItemRepository priceItemRepository) {
        this.pricePageRepository = pricePageRepository;
        this.priceItemRepository = priceItemRepository;
    }

    public Flux<PriceItem> save(Flux<PricePage> pricePageFlux, Flux<PriceItem> priceItemFlux) {
        log.info("save entered");

        try {
            return pricePageRepository
                    .saveAll(pricePageFlux)
                    .thenMany(priceItemRepository.saveAll(priceItemFlux));
        } finally {
            log.info("save left");
        }
    }
}
