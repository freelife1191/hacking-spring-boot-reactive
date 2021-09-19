package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mediatype.alps.Alps.alps;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Created by KMS on 2021/09/20.
 */
@RestController
public class AffordancesItemController {

    private final ItemRepository repository;

    public AffordancesItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/affordances")
    Mono<RepresentationModel<?>> root() {
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> selfLink = linkTo(controller.root()) //
                .withSelfRel() //
                .toMono();

        Mono<Link> itemsAggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM) //
                .toMono();

        return selfLink.zipWith(itemsAggregateLink) //
                .map(links -> Links.of(links.getT1(), links.getT2())) //
                .map(links -> new RepresentationModel<>(links.toList()));
    }

    /**
     * 행동 유도성이 포함된 Item 객체 목록 반환 API
     * @return
     */
    @GetMapping("/affordances/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> aggregateRoot = linkTo(controller.findAll()) //
                .withSelfRel() //
                // andAffordance() 연산을 사용해서 self 링크가 addNewItem()에 대한 링크를 가리키게 한다
                .andAffordance(controller.addNewItem(null)) // <1>
                .toMono();

        // 몽고디비에서 모든 Item 객체를 조회한다
        return this.repository.findAll() // <2>
                // flatMap 연산자에 findOne() 메소드를 인자로 전달한다
                .flatMap(item -> findOne(item.getId())) // <3>
                // 조회 결과를 리스트에 담는다
                .collectList() // <4>
                .flatMap(models -> aggregateRoot //
                        .map(selfLink -> CollectionModel.of( //
                                // Item 객체 목록을 조회할 수 있는 aggregate root 링크와 함께 CollectionModel에 저장한다
                                models, selfLink))); // <5>
    }

    /**
     * 행동 유도성을 통해 GET, PUT을 연결
     * @param id
     * @return
     */
    @GetMapping("/affordances/items/{id}") // <1>
    Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        // AffordancesItemController의 프록시를 생성한다
        AffordancesItemController controller = methodOn(AffordancesItemController.class); // <2>

        Mono<Link> selfLink = linkTo(controller.findOne(id)) //
                .withSelfRel() //
                // andAffordance() 연산을 사용한 것 외에는 HypermediaController에 있던 findOne()과 같다
                // andAffordance()는 Item을 수정할 수 있는 updateItem() 메소드에 사용되는 경로를 findOne() 메소드의 self 링크에 연결한다
                .andAffordance(controller.updateItem(null, id)) // <3>
                .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM) //
                .toMono();

        return Mono.zip(repository.findById(id), selfLink, aggregateLink) //
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    /**
     * 새 Item 추가
     * @param item
     * @return
     */
    @PostMapping("/affordances/items") // <1>
    // 클라이언트가 보낸 데이터에는 링크가 있을 수도 있고 없을 수도 있으며 리액티브 방식으로 처리 된다
    // 앞에서 다룬 PUT 메소드와 상당히 비슷하다
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item) { // <2>
        return item //
                // 클라이언트가 보낸 데이터를 추출한다
                .map(EntityModel::getContent) // <3>
                // 몽고디비에 저장하고 새로 저장된 Item 객체를 flatMap을 통해 다시 Mono에 담는다
                .flatMap(this.repository::save) // <4>
                // getId() 메소드로 변환해서 새로 저장된 Item 객체의 id 값을 얻는다
                .map(Item::getId) // <5>
                // findOne() 메소드를 flatMap의 인자로 전달해서 id에 해당하는 Item의 Mono<EntityModel<Item>>과 링크를 모두 Mono에 담는다
                .flatMap(this::findOne) // <6>
                // 스프링 웹 ResponseEntity.created() 헬퍼 메소드를 사용해서 링크 정보를 Location 헤더에 저장하고 새로 생성된
                // 객체의 내용을 응답 본문에 담아서 반환한다
                .map(newModel -> ResponseEntity.created(newModel // <7>
                        .getRequiredLink(IanaLinkRelations.SELF) //
                        .toUri()).body(newModel.getContent()));
    }

    /**
     * Item 정보 수정
     * @param item
     * @param id
     * @return
     */
    @PutMapping("/affordances/items/{id}") // <1>
    // Mono 타입이므로 이 메소드 호출자가 구독하기 전까지는 실제로 아무것도 동작하지 않는다
    // EntityModel은 클라이언트가 Item 객체를 보낼 수도 있고 하이퍼미디어 형식일 수도 있음을 의미한다
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
                                              @PathVariable String id) {
        return item //
                .map(EntityModel::getContent) //
                // Mono에 담긴 내용물을 꺼내서 새 Item 객체를 생성하고 전달받은 id 값을 새 Item 객체의 식별자로 지정한다
                // 이렇게 하면 몽고디비에 같은 id 값으로 저장돼 있던 레코드를 새로 만든 Item 정보로 덮어쓰거나
                // 몽고디비에 해당 id가 없다면 새 레코드가 추가된다
                .map(content -> new Item(id, content.getName(), // <3>
                        content.getDescription(), content.getPrice())) //
                // 새 Item 객체를 저장한다
                .flatMap(this.repository::save) // <4>
                // 컨트롤러의 findOne() 메소드를 통해서 새로 저장된 객체를 조회한다
                // findOne() 메소드는 몽고디비에서 데이터를 조회해서 하이퍼미디어에 담아서 반환한다
                .then(findOne(id)) // <5>
                // 스프링 프레임워크의 ResponseEntoty 헬퍼 메소드를 사용해서 location 헤더에 self 링크 URI를 담고
                // HTTP 204 No Content 상태 코드를 반환한다
                .map(model -> ResponseEntity.noContent() // <6>
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    @GetMapping(value = "/affordances/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
    public Alps profile() {
        return alps() //
                .descriptor(Collections.singletonList(descriptor() //
                        .id(Item.class.getSimpleName() + "-representation") //
                        .descriptor( //
                                Arrays.stream(Item.class.getDeclaredFields()) //
                                        .map(field -> descriptor() //
                                                .name(field.getName()) //
                                                .type(Type.SEMANTIC) //
                                                .build()) //
                                        .collect(Collectors.toList())) //
                        .build())) //
                .build();
    }
}
