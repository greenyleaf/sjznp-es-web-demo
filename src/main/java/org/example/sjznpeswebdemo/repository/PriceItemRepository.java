package org.example.sjznpeswebdemo.repository;

import org.example.sjznpeswebdemo.entity.PricePage;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface PriceItemRepository extends ReactiveElasticsearchRepository<PricePage, String> {
}
