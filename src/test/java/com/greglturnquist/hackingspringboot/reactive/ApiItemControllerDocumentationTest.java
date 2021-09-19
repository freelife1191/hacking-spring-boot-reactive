package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * Spring REST Docs를 사용하기 위한 테스트 클래스
 * Created by KMS on 2021/09/19.
 */
// WebFlux Controller 테스트에 필ㅇㅛ한 내용만 자동설정되게 한다
@WebFluxTest(controllers = ApiItemController.class)
// Spring REST Docs 사용에 필요한 내용을 자동으로 설정해준다
@AutoConfigureRestDocs
public class ApiItemControllerDocumentationTest {

    // WebFlux Controller 호출에 사용되는 WebTestClient를 자동 주입한다
    @Autowired private WebTestClient webTestClient;
    // @WebFluxTest는 WebFlux Controller 테스트에 필요한 내용만 자동설정하고
    // 서비스를 포함한 그 외 컴포넌트는 생성하지 않는다
    // @MockBean을 붙여주면 해당 클래스의 mock 객체를 자동으로 생성해서 주입해준다
    @MockBean InventoryService service;

    @MockBean ItemRepository repository;

    /**
     * 문서를 자동으로 생성하는 첫 번째 테스트 케이스
     */
    @Test
    void findingAllItems() {
        // Mockito가 제공하는 정적 메소드 호출하면 특정 Item을 반환하도록 지정
        when(repository.findAll()).thenReturn( // <1>
                Flux.just(new Item("item-1", "Alf alarm clock", //
                        "nothing I really need", 19.99)));
        // @MockBean과 Mockito에 의해 미리 정해진 값을 반환
        this.webTestClient.get().uri("/api/items") //
                .exchange() //
                .expectStatus().isOk() // OK인지 검증
                .expectBody() // 응답 본문에 더 여러 가지를 단안할 수 있음
                // document()는 Spring REST Docs 정적 메소드이며
                // 문서 생성 기능을 테스트에 추가하는 역할을 한다
                // 문서는 build/generated-snippets/findAll 디렉터리에 생성된다
                .consumeWith(document("findAll",
                        // 요청 결과로 반환되는 JSON 문자열을 보기 편한 형태로 출력해준다
                        preprocessResponse(prettyPrint())));
    }

    /**
     * 새 객체 추가 테스트 및 문서화
     */
    @Test
    void postNewItem() {
        when(repository.save(any())).thenReturn( //
                Mono.just(new Item("1", "Alf alarm clock", "nothing important", 19.99)));

        this.webTestClient.post().uri("/api/items") // <1>
                // 새로 생성할 객체 정보를 bodyValue() 메소드에 전달해서 요청 본문에 넣는다
                // 객체를 직접 전달할 수도 있고 생성할 객체 정보가 Mono나 Flux에 들어 있다면 Mono나 Flux를 전달할 수도 있다
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) // <2>
                .exchange() //
                // HTTP 201 Created 인지 확인
                .expectStatus().isCreated() // <3>
                .expectBody() //
                // 테스트에서 자동 생성된 문서는 post-new-item 디렉터리에 저장됨
                .consumeWith(document("post-new-item", preprocessResponse(prettyPrint()))); // <4>
    }

    @Test
    void findOneItem() {
        when(repository.findById("item-1")).thenReturn( //
                Mono.just(new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99))); // <1>

        this.webTestClient.get().uri("/api/items/item-1") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("findOne", preprocessResponse(prettyPrint()))); // <2>
    }

    @Test
    void updateItem() {
        when(repository.save(any())).thenReturn( //
                Mono.just(new Item("1", "Alf alarm clock", "updated", 19.99)));

        this.webTestClient.put().uri("/api/items/1") // <1>
                .bodyValue(new Item("Alf alarm clock", "updated", 19.99)) // <2>
                .exchange() //
                .expectStatus().isOk() // <3>
                .expectBody() //
                .consumeWith(document("update-item", preprocessResponse(prettyPrint()))); // <4>
    }
}
