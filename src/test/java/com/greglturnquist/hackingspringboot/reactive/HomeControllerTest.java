package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by KMS on 2021/09/21.
 */
@SpringBootTest
@AutoConfigureWebTestClient
public class HomeControllerTest {

    @Autowired WebTestClient webTestClient;

    @Autowired ItemRepository repository;

    @Test
    void verifyLoginPageBlocksAccess() {
        this.webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "ada")
    void verifyLoginPageWorks() {
        this.webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * 적절한 권한 없이 Item 추가를 시도하는 테스트 작성
     * 403 Forbidden은 사용자가 인증은 됐지만 특정 웹 호출을 할 수 있도록 인가받지는 못했음을 의미
     */
    @Test
    // 스프링 시큐리티의 @WithMockUser를 사용해서 SOME_OTHER_ROLE이라는 역할을 가진 테스트용 가짜 사용자 alice를 테스트에 사용한다
    @WithMockUser(username = "alice", roles = { "SOME_OTHER_ROLE" })
    void addingInventoryWithoutProperRoleFails() {
        // webTestClient를 사용해서 /에 POST 요청을 보내도록 설정한다
        this.webTestClient.post().uri("/")
                // 서버에게 요청을 전송하고 응답을 받는다
                .exchange()
                // HTTP 403 Forbidden 상태 코드가 반환되는지 확인한다
                .expectStatus().isForbidden();
    }

    /**
     * 적절한 인가를 받은 새 Item 추가 요청
     */
    @Test
    // INVENTORY 역할을 가진 가짜 사용자 bob을 테스트에 사용한다
    @WithMockUser(username = "bob", roles = { "INVENTORY" })
    void addingInventoryWithProperRoleSucceeds() {
        this.webTestClient
                .post().uri("/")
                // Content-Type 헤더값을 application/json으로 지정해서 요청 본문에 JSON 데이터가 전송될 것임을 알린다
                .contentType(MediaType.APPLICATION_JSON)
                // JSON 문자열로 된 새 Item 정보를 요청 본문에 담는다
                .bodyValue("{" +
                        "\"name\": \"iPhone 11\", " +
                        "\"description\": \"upgrade\", " +
                        "\"price\": 999.99" +
                        "}")
                .exchange()
                // 서버에 요청을 보내고 응답을 받은 후에 200 Ok가 반환되는지 확인한다
                .expectStatus().isOk();

        // 주입받은 ItemRepository를 사용해서 몽고디비에 쿼리를 날려서 새 Item이 추가됐는지 확인한다
        this.repository.findByName("iPhone 11")
                // 리액터 응답을 검증하기 위해 StepVerifier로 감싼다
                .as(StepVerifier::create)
                // 새로 생성된 Item의 설명 항목과 가격 항목의 값을 단언문을 통해 확인한다
                // 새로 생성된 Item의 이름은 이미 쿼리를 날리면서 검증한 것과 마찬가지이므로 별도로 확인할 필요는 없다
                .expectNextMatches(item -> {
                    assertThat(item.getDescription()).isEqualTo("upgrade");
                    assertThat(item.getPrice()).isEqualTo(999.99);
                    // 단언문이 모두 성공할 때만 true를 반환한다
                    // expectNextMatches()는 인자로 받은 조건식(Predicate)이 true를 반환하면 테스트를 통과시키고
                    // false를 반환하면 테스트를 실패시킨다
                    return true;
                })
                // 리액티브 스트림의 완료 신호가 전송됐는지도 확인한다
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "carol", roles = { "SOME_OTHER_ROLE" })
    void deletingInventoryWithoutProperRoleFails() {
        this.webTestClient.delete().uri("/some-item") //
                .exchange() //
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "dan", roles = { "INVENTORY" })
    void deletingInventoryWithProperRoleSucceeds() {
        String id = this.repository.findByName("Alf alarm clock") //
                .map(Item::getId) //
                .block();

        this.webTestClient //
                .delete().uri("/" + id) //
                .exchange() //
                .expectStatus().isOk();

        this.repository.findByName("Alf alarm clock") //
                .as(StepVerifier::create) //
                .expectNextCount(0) //
                .verifyComplete();
    }
}
