package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class PriceItemRepositoryTest {
    @Autowired
    PriceItemRepository priceItemRepository;

    @Test
    void findAllTest() {
        Long aLong = priceItemRepository.count().block();
        log.info("count: {}", aLong);
    }
}
