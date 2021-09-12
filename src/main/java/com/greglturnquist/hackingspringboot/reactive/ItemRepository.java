package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * 판매 상품(Item)용 리액티브 데이터 리포지토리
 * Created by KMS on 2021/09/11.
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
    // 검색어로 상품 목록을 조회하는 리포지토리
    Flux<Item> findByNameContaining(String partialName);
    // end::code[]

    // tag::code-2[]
    @Query("{ 'name' : ?0, 'age' :  }")
    Flux<Item> findItemsForCustomerMonthlyReport();
    //
    @Query(sort = "{ 'age' : -1", value = "{ 'name' : 'TV tray', 'age' : }")
    Flux<Item> findSortedStuffForWeeklyReport();
    // end::code-2[]

    // 요구 사항을 반영하면서 복잡해진 리포지토리
    // tag::code-3[]
    // name 검색
    Flux<Item> findByNameContainingIgnoreCase(String partialName);

    // description 검색
    Flux<Item> findByDescriptionContainingIgnoreCase(String partialName);

    // name And description 검색
    Flux<Item> findByNameContainingAndDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

    // name OR description 검색
    Flux<Item> findByNameContainingOrDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);
    // end::code-3[]

}
