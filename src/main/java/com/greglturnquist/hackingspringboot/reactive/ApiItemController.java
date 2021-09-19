package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Created by KMS on 2021/09/17.
 */
@RestController
public class ApiItemController {

    private final ItemRepository repository;

    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }

    /**
     * 모든 상품을 반환하는 API
     * @return
     */
    @GetMapping("/api/items") // <1>
    Flux<Item> findAll() { // <2>
        return this.repository.findAll(); // <3>
    }

    /**
     * 한 개의 Item을 조회하는 API
     * @param id
     * @return
     */
    @GetMapping("/api/items/{id}") // <1>
    Mono<Item> findOne(@PathVariable String id) { // <2>
        return this.repository.findById(id); // <3>
    }

    /**
     * 새 Item을 생성하는 API
     * @param item
     * @return
     */
    @PostMapping("/api/items") // <1>
    // 인자 타입이 리액터 타입인 Mono이므로 이 요청 처리를 위한 리액티브 플로우에서 구독이 발생하지 않으면
    // 요청 본문을 Item 타입으로 역직렬화하는 과정도 실행되지 않는다
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) { // <2>
        // 이 단계의 목표는 데이터를 저장하는 것이므로 요청 본문으로 들어온 item 객체에 map이나 flatMap 연산을 적용하면 된다
        // 인자로 받은 Mono에서 꺼낸 후에 스프링 데이터의 save() 메소드에 전달되고 다시 Mono를 반환하므로 flatMap을 사용해야 한다
        return item.flatMap(this.repository::save) // <3>
                .map(savedItem -> ResponseEntity
                        // ResponseEntity 클래스에는 ok(), created(), accepted(), noContent(), badRequest(), notFound() 등
                        // 응답 메시지를 편리하게 구성할 수 있는 다양한 메소드가 포함돼 있다
                        .created(URI.create("/api/items/"+savedItem.getId())) // <4>
                        // saveItem 객체를 직렬화해서 응답 본문에 적는 일은 스프링 웹플럭스가 담당한다
                        .body(savedItem)); // <5>
    }

    /**
     * 기존 Item 객체 교체
     * @param item
     * @param id
     * @return
     */
    // HTTP PUT은 교체를 의미하며, 교체 대상이 존재하지 않으면 새로 생성
    @PutMapping("/api/items/{id}") // <1>
    public Mono<ResponseEntity<?>> updateItem( //
               @RequestBody Mono<Item> item, // <2>
               @PathVariable String id) { // <3>

        return item //
                .map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice())) // <4>
                // save 메소드는 Item 객체를 Mono에 담아 반환하고 flatMap 메소드는 Item 객체를 꺼내서 다시 Mono에 담아 반환한다
                .flatMap(this.repository::save) // <5>
                // thenReturn() 메소드는 스프링 웹의 ResponseEntity.ok() 헬퍼 메소드를 사용해서 교체 후 데이터를 HTTP 200 OK와 함께 반환한다
                // 스프링 데이터에서 제공하는 save()나 delete() 메소드를 사용하고 이후에 then***() 메소드를 호출할 때는 항상 flatMap()을 사용해야 한다
                // 그렇지 않으면 저장도 삭제도 되지 않는다
                // flatMap()을 사용해서 결괏값을 꺼내야 데이터 스토어에도 변경이 적용된다
                .map(ResponseEntity::ok); // <6>
    }
}
