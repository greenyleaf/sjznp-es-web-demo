package org.example.sjznpeswebdemo.repository;

import org.example.sjznpeswebdemo.entity.PriceItem;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface PriceItemRepository extends ReactiveElasticsearchRepository<PriceItem, String> {
    Mono<PriceItem> findFirstByOrderByDateDesc();

    Mono<PriceItem> findFirstByOrderByDate();

    Mono<PriceItem> findTopBy();

    Mono<Long> countByDate(LocalDate date);
}
