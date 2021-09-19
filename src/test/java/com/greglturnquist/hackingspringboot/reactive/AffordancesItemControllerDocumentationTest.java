package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

/**
 * 행동 유도성 기반 문서화 테스트 클래스
 * Created by KMS on 2021/09/20.
 */
// AffordancesItemController를 호출하는 데 필요한 빈만 애플리케이션 컨텍스트에 로딩한다
@WebFluxTest(controllers = AffordancesItemController.class)
// Spring REST Docs을 사용하기 위한 자동설정을 적용한다
@AutoConfigureRestDocs
public class AffordancesItemControllerDocumentationTest {
    // 컨트롤러 메소드를 호출하는 데 필요한 WebTestClient를 주입받는다
    @Autowired private WebTestClient webTestClient;
    // InventoryService의 mock 객체를 빈으로 주입받는다
    @MockBean InventoryService service;
    // ItemRepository의 mock 객체를 빈으로 주입받는다
    @MockBean ItemRepository repository;

    /**
     * Spring REST Docs을 활용한 행동 유도성 테스트
     * HAL-FORM 형식에는 응답 데이터와 탐색 가능한 링크뿐만 아니라 PUT 요청을 보낼 때 필요한 메타데이터까지
     * _templates 항목에 표시해준다
     */
    @Test
    void findSingleItemAffordances() {
        when(repository.findById("item-1")).thenReturn(Mono.just( //
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/affordances/items/item-1") // <1>
                .accept(MediaTypes.HAL_FORMS_JSON) // <2>
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("single-item-affordances", //
                        preprocessResponse(prettyPrint()))); // <3>
    }

    @Test
    void findAggregateRootItemAffordances() {
        when(repository.findAll()).thenReturn(Flux.just( //
                new Item("Alf alarm clock", "nothing I really need", 19.99)));
        when(repository.findById((String) null)).thenReturn(Mono.just( //
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        this.webTestClient.get().uri("/affordances/items") // <1>
                .accept(MediaTypes.HAL_FORMS_JSON) // <2>
                .exchange() //
                .expectStatus().isOk() //
                .expectBody() //
                .consumeWith(document("aggregate-root-affordances", preprocessResponse(prettyPrint()))); // <3>
    }
}
