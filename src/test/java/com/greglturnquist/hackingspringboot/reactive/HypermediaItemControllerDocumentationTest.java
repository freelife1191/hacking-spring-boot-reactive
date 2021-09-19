package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * 하이퍼미디어 API 테스트 클래스
 * Created by KMS on 2021/09/20.
 */
@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
public class HypermediaItemControllerDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean InventoryService service;

    @MockBean ItemRepository repository;

    @Test
    void findingAllItems() {
        when(repository.findAll()) //
                .thenReturn(Flux.just( //
                        new Item("Alf alarm clock", //
                                "nothing I really need", 19.99)));
        when(repository.findById((String) null)) //
                .thenReturn(Mono.just( //
                        new Item("item-1", "Alf alarm clock", //
                                "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/hypermedia/items") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("findAll-hypermedia", //
                        preprocessResponse(prettyPrint()))); //
    }
    // end::test1[]

    // tag::test2[]
    // @Test
    void postNewItem() {
        this.webTestClient.post().uri("/hypermedia/items") //
                .body(Mono.just( //
                        new Item("item-1", "Alf alarm clock", //
                                "nothing I really need", 19.99)),
                        Item.class) //
                .exchange() //
                .expectStatus().isCreated() //
                .expectBody().isEmpty();
    }
    // end::test2[]

    /**
     * 하이퍼미디어 엔드포인트 테스트
     */
    @Test
    void findOneItem() {
        when(repository.findById("item-1")).thenReturn(Mono.just( //
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/hypermedia/items/item-1") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()),
                        // Spring REST Docs의 HypermediaDocumentation 클래스의 links() 메소드를 호출해서 응답에 링크가 포함된 문서 조각을 만든다
                        links(
                                // Item 객체 자신을 나타내는 self 링크를 찾고 description()에 전달된 설명과 함께 문서화 한다
                                linkWithRel("self").description("이 `Item`에 대한 공식 링크"), // <2>
                                // aggregate root로 연결되는 item 링크를 찾고 description()에 전달된 설명과 함께 문서화 한다
                                linkWithRel("item").description("`Item` 목록 링크")))); // <3>
    }
    // end::test3[]

    @Test
    void findProfile() {
        this.webTestClient.get().uri("/hypermedia/items/profile") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("profile", //
                        preprocessResponse(prettyPrint())));
    }
}
