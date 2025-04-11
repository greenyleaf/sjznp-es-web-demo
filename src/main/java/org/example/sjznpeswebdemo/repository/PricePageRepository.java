package org.example.sjznpeswebdemo.repository;

import org.example.sjznpeswebdemo.entity.PricePage;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricePageRepository extends ReactiveElasticsearchRepository<PricePage, String> {
}
