package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * 판매 상품(Item)용 리액티브 데이터 리포지토리
 * Created by KMS on 2021/09/11.
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
}