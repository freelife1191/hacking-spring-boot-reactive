package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Mono;

/**
 * Created by KMS on 2021/09/21.
 */
public interface UserRepository extends CrudRepository<User, String> {
    Mono<User> findByName(String name);
}