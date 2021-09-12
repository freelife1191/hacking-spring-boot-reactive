package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 웹플럭스 슬라이스 테스트
 * Created by KMS on 2021/09/12.
 */
// tag::code[]
@WebFluxTest(HomeController.class) // <1>HomeController에 국한된 스프링 웹플럭스 슬라이스 테스트를 사용하도록 설정
public class HomeControllerSliceTest {

    @Autowired // <2>웹플럭스 슬라이스 테스트의 일부로서 WebTestClient 인스턴스가 생성되고 주입된다
    private WebTestClient client;

    // 테스트 대상인 HomeController의 협력자인 InventoryService는 실제 객체가 아닌 가짜 객체를 만들어 사용함으로써
    // 테스트가 협력자가 아닌 테스트 대상에 집중하게 만든다
    @MockBean // <3>
    InventoryService inventoryService;

    @Test
    void homePage() {
        when(inventoryService.getInventory()).thenReturn(Flux.just( //
                new Item("id1", "name1", "desc1", 1.99), //
                new Item("id2", "name2", "desc2", 9.99) //
        ));
        when(inventoryService.getCart("My Cart")) //
                .thenReturn(Mono.just(new Cart("My Cart")));

        client.get().uri("/").exchange() //
                .expectStatus().isOk() //
                .expectBody(String.class) //
                .consumeWith(exchangeResult -> {
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
                });
    }
}
// end::code[]