package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

/**
 * Created by KMS on 2021/09/11.
 */
public interface ItemByExampleRepository extends ReactiveQueryByExampleExecutor<Item> {
}
