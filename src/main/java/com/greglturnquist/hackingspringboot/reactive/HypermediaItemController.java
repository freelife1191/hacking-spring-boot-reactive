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
public class HypermediaItemController {

    private final ItemRepository repository;

    public HypermediaItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hypermedia")
    Mono<RepresentationModel<?>> root() {
        HypermediaItemController controller = //
                methodOn(HypermediaItemController.class);

        Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();

        Mono<Link> itemsAggregateLink = //
                linkTo(controller.findAll()) //
                        .withRel(IanaLinkRelations.ITEM) //
                        .toMono();

        return selfLink.zipWith(itemsAggregateLink) //
                .map(links -> Links.of(links.getT1(), links.getT2())) //
                .map(links -> new RepresentationModel<>(links.toList()));
    }

    @GetMapping("/hypermedia/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {

        return this.repository.findAll() //
                .flatMap(item -> findOne(item.getId())) //
                .collectList() //
                .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class) //
                        .findAll()).withSelfRel() //
                        .toMono() //
                        .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
    }

    /**
     * 한 개의 Item 객체에 대한 하이퍼미디어 생성
     * @param id
     * @return
     */
    @GetMapping("/hypermedia/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        // Spring HATEOS의 정적 메소드인 WebFluxLinkBuilder.methodOn() 연산자를 사용해서
        // 컨트롤러에 대한 프록시를 생성한다
        HypermediaItemController controller = methodOn(HypermediaItemController.class); // <1>

        // WebFluxLinkBuilder.linkTo() 연산자를 사용해서 컨트롤러의 findOne() 메소드에 대한 링크를 생성한다
        // 현재 메소드가 findOne() 메소드이므로 self라는 이름의 링크를 추가하고 리액터 Mono에 담아 반환한다
        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono(); // <2>

        // 모든 상품을 반환하는 findAll() 메소드를 찾아서 aggregate root에 대한 링크를 생성한다
        // IANA(Internet Assigned Numbers Authority, 인터넷 할당 번호 관리기관)표준에 따라 링크 이름을 item으로 명명한다
        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM).toMono(); // <3>

        // 여러 개의 비동기 요청을 실행하고 각 결과를 하나로 합치기 위해 Mono.zip() 메소드를 사용한다
        // 예제에서는 findById() 메소드 호출과 selfLink, aggretateLink 생성 요청 결과를
        // 타입 안정성이 보장되는 리액터 Tuple 타입에 넣고 Mono로 감싸서 반환한다
        return Mono.zip(repository.findById(id), selfLink, aggregateLink) // <4>
                // 마지막으로 map()을 통해 Tuple에 담겨 있던 여러 비동기 요청 결과를 꺼내서 EntityModel을 만들고 Mono로 감싸서 반환한다
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3()))); // <5>
    }

    @GetMapping("/hypermedia/items/{id}/affordances")
    Mono<EntityModel<Item>> findOneWithAffordances(@PathVariable String id) {
        HypermediaItemController controller = //
                methodOn(HypermediaItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel() //
                .andAffordance(controller.updateItem(null, id)) // <2>
                .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll()).withRel(IanaLinkRelations.ITEM) //
                .toMono();

        return Mono.zip(repository.findById(id), selfLink, aggregateLink) //
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    @PostMapping("/hypermedia/items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item) {
        return item //
                .map(EntityModel::getContent) //
                .flatMap(this.repository::save) //
                .map(Item::getId) //
                .flatMap(this::findOne) //
                .map(newModel -> ResponseEntity.created(newModel //
                        .getRequiredLink(IanaLinkRelations.SELF) //
                        .toUri()).build());
    }

    @PutMapping("/hypermedia/items/{id}") // <1>
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
                                              @PathVariable String id) {
        return item //
                .map(EntityModel::getContent) //
                .map(content -> new Item(id, content.getName(), // <3>
                        content.getDescription(), content.getPrice())) //
                .flatMap(this.repository::save) // <4>
                .then(findOne(id)) // <5>
                .map(model -> ResponseEntity.noContent() // <6>
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    /**
     * 메타데이터를 포함한 ALPS 프로파일 생성
     * @return
     */
    @GetMapping(value = "/hypermedia/items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
    public Alps profile() {
        return alps() //
                .descriptor(Collections.singletonList(descriptor() //
                        .id(Item.class.getSimpleName() + "-repr") //
                        .descriptor(Arrays.stream( //
                                Item.class.getDeclaredFields()) //
                                .map(field -> descriptor() //
                                        .name(field.getName()) //
                                        .type(Type.SEMANTIC) //
                                        .build()) //
                                .collect(Collectors.toList())) //
                        .build())) //
                .build();
    }
}
