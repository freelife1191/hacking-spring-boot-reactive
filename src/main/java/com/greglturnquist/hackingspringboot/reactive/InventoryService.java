package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by KMS on 2021/09/11.
 */
@Service
public class InventoryService {
    private ItemRepository repository;
    private ReactiveFluentMongoOperations fluentOperations;

    InventoryService(ItemRepository repository, //
                     ReactiveFluentMongoOperations fluentOperations) {
        this.repository = repository;
        this.fluentOperations = fluentOperations;
    }

    Flux<Item> getItems() {
        // imagine calling a remote service!
        return Flux.empty();
    }

    /**
     * 이름, 설명, AND 사용 여부를 모두 적용한 복잡한 필터링 구현
     * @param partialName
     * @param partialDescription
     * @param useAnd
     * @return
     */
    // tag::code-2[]
    Flux<Item> search(String partialName, String partialDescription, boolean useAnd) {
        if (partialName != null) {
            if (partialDescription != null) {
                if (useAnd) {
                    return repository //
                            .findByNameContainingAndDescriptionContainingAllIgnoreCase( //
                                    partialName, partialDescription);
                } else {
                    return repository.findByNameContainingOrDescriptionContainingAllIgnoreCase( //
                            partialName, partialDescription);
                }
            } else {
                return repository.findByNameContaining(partialName);
            }
        } else {
            if (partialDescription != null) {
                return repository.findByDescriptionContainingIgnoreCase(partialDescription);
            } else {
                return repository.findAll();
            }
        }
    }
    // end::code-2[]

    /**
     * 복잡한 검색 요구 조건을 Example 쿼리로 구현한 코드
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    // tag::code-3[]
    Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0); // <1>

        ExampleMatcher matcher = (useAnd // <2> 사용자가 선택한 useAnd 값에 따라 3항 연산자로 분기해서 ExampleMatcher 생성
                ? ExampleMatcher.matchingAll() //
                : ExampleMatcher.matchingAny()) //
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // <3>StringMatcher.CONTAINING을 사용해서 부분 일치 검색을 수행
                .withIgnoreCase() // <4>대소문자는 구분하지 않는다
                // ExampleMatcher는 기본적으로 null 필드를 무시하지만 기본타입인 double에는 null이 올 수 없으므로 price 필드가 무시되도록 명시적 지정
                .withIgnorePaths("price"); // <5>

        Example<Item> probe = Example.of(item, matcher); // <6>Item 객체와 matcher를 함께 Example.of(...)로 감싸서 Example을 생성

        return repository.findAll(probe); // <7>쿼리를 실행
    }
    // end::code-3[]

    /**
     * 평문형 API를 사용한 Item 검색
     * @param name
     * @param description
     * @return
     */
    // tag::code-4[]
    Flux<Item> searchByFluentExample(String name, String description) {
        return fluentOperations.query(Item.class) //
                // 몽고디비에서 { $and: [{ name: 'TV tray' }, { description: 'Smurf' }] }
                .matching(query(where("TV tray").is(name).and("Smurf").is(description))) //
                .all();
    }
    // end::code-4[]

    /**
     * 평문형 API를 사용한 Example 쿼리 검색 구현 코드
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    // tag::code-5[]
    Flux<Item> searchByFluentExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd //
                ? ExampleMatcher.matchingAll() //
                : ExampleMatcher.matchingAny()) //
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //
                .withIgnoreCase() //
                .withIgnorePaths("price");

        return fluentOperations.query(Item.class) //
                .matching(query(byExample(Example.of(item, matcher)))) //
                .all();
    }
    // end::code-5[]
}
