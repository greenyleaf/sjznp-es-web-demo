package org.example.sjznpeswebdemo.repository;

import org.example.sjznpeswebdemo.entity.PricePage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface PricePageRepository extends ReactiveElasticsearchRepository<PricePage, String> {
    Mono<PricePage> findFirstByOrderByDateDesc();

    Mono<PricePage> findFirstByOrderByDate();

    Flux<PricePage> findByOrderByDateAscPageNo(Pageable pageable);

    Mono<Long> countByDate(LocalDate date);
}
