package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.greglturnquist.hackingspringboot.reactive.SecurityConfig.INVENTORY;
import static org.springframework.hateoas.mediatype.alps.Alps.alps;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Created by KMS on 2021/09/21.
 */
@RestController
public class ApiItemController {

    private static final SimpleGrantedAuthority ROLE_INVENTORY =
            new SimpleGrantedAuthority("ROLE_" + INVENTORY);

    private final ItemRepository repository;

    public ApiItemController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api")
    Mono<RepresentationModel<?>> root() {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.root()).withSelfRel()
                .toMono();

        Mono<Link> itemsAggregateLink = linkTo(controller.findAll(null))
                .withRel(IanaLinkRelations.ITEM)
                .toMono();

        return Mono.zip(selfLink, itemsAggregateLink)
                .map(links -> Links.of(links.getT1(), links.getT2()))
                .map(links -> new RepresentationModel<>(links.toList()));
    }

    @GetMapping("/api/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll(Authentication auth) {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findAll(auth)).withSelfRel().toMono();

        Mono<Links> allLinks;

        if (auth.getAuthorities().contains(ROLE_INVENTORY)) {
            Mono<Link> addNewLink = linkTo(controller.addNewItem(null, auth)).withRel("add").toMono();

            allLinks = Mono.zip(selfLink, addNewLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        } else {
            allLinks = selfLink
                    .map(link -> Links.of(link));
        }

        return allLinks
                .flatMap(links -> this.repository.findAll()
                        .flatMap(item -> findOne(item.getId(), auth))
                        .collectList()
                        .map(entityModels -> CollectionModel.of(entityModels, links)));
    }

    /**
     * 인가 정보 기반의 링크 정보 제어
     * @param id
     * @param auth
     * @return
     */
    @GetMapping("/api/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id, Authentication auth) {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id, auth)).withSelfRel()
                .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll(auth))
                .withRel(IanaLinkRelations.ITEM).toMono();

        // 사용자에게 반환할 링크 정보를 Mono<Links> 타입의 allLinks에 담을 것이다
        // Mono<Links>는 Spring HATEOS의 링크 데이터 모음인 Links 타입을 원소로 하는 리액터 버전 컬렉션이다
        Mono<Links> allLinks;

        // 사용자가 ROLE_INVENTORY 권한을 가지고 있는지 검사해서 가지고 있으면 DELETE 기능에 대한 링크를
        // self와 aggregate root 링크와 함께 allLinks에 포함한다
        // 리액터의 Mono.zip() 연산은 주어진 3개의 링크를 병합한 후 튜플(tuple)로 만든다
        // 그리고 map()을 통해 Links 객체로 변환한다
        if (auth.getAuthorities().contains(ROLE_INVENTORY)) {
            Mono<Link> deleteLink = linkTo(controller.deleteItem(id)).withRel("delete")
                    .toMono();
            allLinks = Mono.zip(selfLink, aggregateLink, deleteLink)
                    .map(links -> Links.of(links.getT1(), links.getT2(), links.getT3()));
        // 사용자가 ROLE_INVENTORY 권한을 가지고 있지 않다면 self 링크만 aggregate root 링크에 포함한다
        } else {
            allLinks = Mono.zip(selfLink, aggregateLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        }

        return this.repository.findById(id)
                // 데이터 스토어에서 Item 객체를 조회하고 Links 정보를 추가해서 Spring HATEOS의 EntityModel 컨테이너로 변환해서 반환한다
                .zipWith(allLinks)
                .map(o -> EntityModel.of(o.getT1(), o.getT2()));
    }

    /**
     * 인가된 사용자에 의해서만 새 Item 생성 가능
     * @param item
     * @param auth
     * @return
     */
    // @PreAuthorize는 메소드 수준에서 보안을 적용할 수 있게 해주는 스프링 시큐리티의 핵심 애너테이션이다
    // 스프링 시큐리티 SpEL(Spring Expression Language) 표현식을 사용해서 이 메소드를 호출하는 사용자가
    // ROLE_INVENTORY 역할을 가지고 있는지 단언한다
    // INVENTORY는 앞에서 "INVENTORY"라는 문자열값을 가진 단순한 상수다
    @PreAuthorize("hasRole('" + INVENTORY + "')")
    @PostMapping("/api/items/add")
    // 이 메소드도 Authentication 객체를 인자로 받는다
    // 어떤 이유에서든 메소드가 현재 사용자의 보안 컨텍스트를 사용할 필요가 있다면 이 방식으로 주입받을 수 있다
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Item item, Authentication auth) { // <3>
        return this.repository.save(item) //
                .map(Item::getId) //
                .flatMap(id -> findOne(id, auth)) //
                .map(newModel -> ResponseEntity.created(newModel //
                        .getRequiredLink(IanaLinkRelations.SELF) //
                        .toUri()).build());
    }

    /**
     * 메소드 수준 보안이 적용된 Item 삭제 메소드
     * @param id
     * @return
     */
    // 이 메소드도 앞에서 새 Item을 생성할 때와 똑같은 @PreAuthorize 애너테이션이 붙어있다
    // 따라서 ROLE_INVENTORY 권한을 가진 사용자만 이 메소드를 실행할 수 있다
    @PreAuthorize("hasRole('" + INVENTORY + "')")
    @DeleteMapping("/api/items/delete/{id}")
    Mono<ResponseEntity<?>> deleteItem(@PathVariable String id) {
        // 몽고디비에 삭제를 요청한다
        return this.repository.deleteById(id)
                // 삭제 처리 성공을 의미하는 HTTP 204 No Content 응답을 반환한다
                .thenReturn(ResponseEntity.noContent().build());
    }

    // tag::update-item[]
    @PutMapping("/api/items/{id}") // <1>
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
                                              @PathVariable String id, Authentication auth) {
        return item //
                .map(EntityModel::getContent) //
                .map(content -> new Item(id, content.getName(), // <3>
                        content.getDescription(), content.getPrice())) //
                .flatMap(this.repository::save) // <4>
                .then(findOne(id, auth)) // <5>
                .map(model -> ResponseEntity.noContent() // <6>
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }
    // end::update-item[]

    // tag::profile[]
    @GetMapping(value = "/api/items/profile"/*, produces = MediaTypes.ALPS_JSON_VALUE*/)
    public Alps profile() {
        return alps() //
                .descriptor(Collections.singletonList(descriptor() //
                        .id(Item.class.getSimpleName() + "-representation") //
                        .descriptor(Arrays.stream(Item.class.getDeclaredFields()) //
                                .map(field -> descriptor() //
                                        .name(field.getName()) //
                                        .type(Type.SEMANTIC) //
                                        .build()) //
                                .collect(Collectors.toList())) //
                        .build())) //
                .build();
    }
    // end::profile[]
}
