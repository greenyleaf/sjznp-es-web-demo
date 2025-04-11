package org.example.sjznpeswebdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.config.EnableReactiveElasticsearchAuditing;

@SpringBootApplication
@EnableReactiveElasticsearchAuditing
public class SjznpEsWebDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SjznpEsWebDemoApplication.class, args);
    }

}
