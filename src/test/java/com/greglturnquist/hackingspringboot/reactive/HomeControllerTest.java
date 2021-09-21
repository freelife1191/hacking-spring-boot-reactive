package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by KMS on 2021/09/21.
 */
@WebFluxTest(controllers = HomeController.class)
public class HomeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean InventoryService service;

    @Test
    void verifyLoginPageBlocksAccess() {
        this.webTestClient.get().uri("/") //
                .exchange() //
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "ada")
    void verifyLoginPageWorks() {
        when(this.service.getInventory()).thenReturn(Flux.just( //
                new Item("1", "Alf alarm clock", "kids clock", 19.99), //
                new Item("2", "Smurf TV tray", "kids TV tray", 24.99)));

        when(this.service.getCart(any())).thenReturn(Mono.just(new Cart("Test Cart")));

        this.webTestClient.get().uri("/") //
                .exchange() //
                .expectStatus().isOk();
    }
}
