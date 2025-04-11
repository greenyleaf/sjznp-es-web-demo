package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class PricePageRepositoryTest {
    @Autowired
    PricePageRepository pricePageRepository;

    @Test
    void findAllTest() {
        Long aLong = pricePageRepository.count().block();
        log.info("count: {}", aLong);
    }
}
