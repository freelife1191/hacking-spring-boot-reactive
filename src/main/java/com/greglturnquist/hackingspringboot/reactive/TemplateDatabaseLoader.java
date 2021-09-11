package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

/**
 * MongoOperations 기반 데이터베이스 로더
 * Created by KMS on 2021/09/11.
 */
@Component
public class TemplateDatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(new Item("Alf alarm clock", 19.99));
            mongo.save(new Item("Smurf TV tray", 24.99));
        };
    }
}
