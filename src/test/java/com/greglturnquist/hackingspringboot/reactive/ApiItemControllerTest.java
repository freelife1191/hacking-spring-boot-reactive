package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

/**
 * 하이퍼미디어 탐색 기능을 갖춘 WebTestClient 설정
 * Created by KMS on 2021/09/21.
 */
@SpringBootTest
// HAL을 사용하려면 @EnableHypermidiaSupport 애너테이션을 어딘가에 명시해야 한다
// Spring HATEOS가 Classpath에 있다면 스프링 부트 애플리케이션이 자동으로 @EnableHypermediaSupport 애너테이션을 적용한다
// 이번 테스트 케이스에서는 애너테이션을 직접 명시했다
@EnableHypermediaSupport(type = HAL)
@AutoConfigureWebTestClient
public class ApiItemControllerTest {
    // @AutoConfigureWebTestClient에 의해 생성된 WebTestClient를 주입받는다
    @Autowired WebTestClient webTestClient;

    @Autowired ItemRepository repository;
    // Spring HATEOS의 HypermediaWebTestClientConfigurer를 주입받는다
    @Autowired HypermediaWebTestClientConfigurer webClientConfigurer;

    @BeforeEach
    void setUp() {
        // webClientConfigurer를 webTestClient에 적용하면 활성화된 모든 하이퍼미디어 타입에 대한 지원 기능을 등록한다
        this.webTestClient = this.webTestClient.mutateWith(webClientConfigurer);
    }

    @Test
    void noCredentialsFailsAtRoot() {
        this.webTestClient.get().uri("/api") //
                .exchange() //
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "ada")
    void credentialsWorksOnRoot() {
        this.webTestClient.get().uri("/api") //
                .exchange() //
                .expectStatus().isOk() //
                .expectBody(String.class) //
                .isEqualTo("{\"_links\":{\"self\":{\"href\":\"/api\"},\"item\":{\"href\":\"/api/items\"}}}");
    }

    /**
     * 비인가 사용자 테스트
     */
    @Test
    // 스프링 시큐리티의 @WithMockUser를 써서 SOME_OTHER_ROLE이라는 역할이 부여된 인가되지 않은 가짜 사용자를 테스트에 사용한다
    @WithMockUser(username = "alice", roles = { "SOME_OTHER_ROLE" })
    void addingInventoryWithoutProperRoleFails() {
        this.webTestClient
                .post().uri("/api/items/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{" +
                        "\"name\": \"iPhone X\", " +
                        "\"description\": \"upgrade\", " +
                        "\"price\": 999.99" +
                        "}")
                .exchange()
                // HTTP 403 Forbidden 응답 코드가 반환되면 인증은 된 사용자이지만 새 Item 추가 기능은 인가받지 못했음을 의미한다
                // pathMatcher()를 사용하지 않고도 메소드 수준에서 적절한 보안 조치가 적용됐음을 확인할 수 있다
                .expectStatus().isForbidden();
    }

    /**
     * 인가된 사용자 테스트
     */
    @Test
    // INVENTORY 역할을 가진 가짜 사용자 bob 사용
    @WithMockUser(username = "bob", roles = { "INVENTORY" })
    void addingInventoryWithProperRoleSucceeds() {
        this.webTestClient
                .post().uri("/api/items/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{" +
                        "\"name\": \"iPhone X\", " +
                        "\"description\": \"upgrade\", " +
                        "\"price\": 999.99" +
                        "}")
                .exchange()
                // HTTP 201 Created 상태 코드 반환 확인
                .expectStatus().isCreated();

        // 데이터베이스를 조회해서 새 Item이 정상적으로 저장됐는지 확인
        this.repository.findByName("iPhone X")
                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getDescription()).isEqualTo("upgrade");
                    assertThat(item.getPrice()).isEqualTo(999.99);
                    return true;
                }) //
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "carol", roles = { "SOME_OTHER_ROLE" })
    void deletingInventoryWithoutProperRoleFails() {
        this.webTestClient.delete().uri("/api/items/delete/some-item") //
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
                .delete().uri("/api/items/delete/" + id) //
                .exchange() //
                .expectStatus().isNoContent();

        this.repository.findByName("Alf alarm clock") //
                .as(StepVerifier::create) //
                .expectNextCount(0) //
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "alice")
    void navigateToItemWithoutInventoryAuthority() {
        RepresentationModel<?> root = this.webTestClient.get().uri("/api") //
                .exchange() //
                .expectBody(RepresentationModel.class) //
                .returnResult().getResponseBody();

        CollectionModel<EntityModel<Item>> items = this.webTestClient.get() //
                .uri(root.getRequiredLink(IanaLinkRelations.ITEM).toUri()) //
                .exchange() //
                .expectBody(new CollectionModelType<EntityModel<Item>>() {}) //
                .returnResult().getResponseBody();

        assertThat(items.getLinks()).hasSize(1);
        assertThat(items.hasLink(IanaLinkRelations.SELF)).isTrue();

        EntityModel<Item> first = items.getContent().iterator().next();

        EntityModel<Item> item = this.webTestClient.get() //
                .uri(first.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .exchange() //
                .expectBody(new EntityModelType<Item>() {}) //
                .returnResult().getResponseBody();

        assertThat(item.getLinks()).hasSize(2);
        assertThat(item.hasLink(IanaLinkRelations.SELF)).isTrue();
        assertThat(item.hasLink(IanaLinkRelations.ITEM)).isTrue();
    }

    /**
     * 하이퍼미디어 테스트
     */
    @Test
    @WithMockUser(username = "alice", roles = { "INVENTORY" })
    void navigateToItemWithInventoryAuthority() {

        // /api에 GET 요청
        RepresentationModel<?> root = this.webTestClient.get().uri("/api")
                .exchange()
                .expectBody(RepresentationModel.class)
                .returnResult().getResponseBody();

        // Item의 aggregate root 링크에 GET 요청
        CollectionModel<EntityModel<Item>> items = this.webTestClient.get()
                .uri(root.getRequiredLink(IanaLinkRelations.ITEM).toUri())
                .exchange()
                .expectBody(new CollectionModelType<EntityModel<Item>>() {})
                .returnResult().getResponseBody();

        assertThat(items.getLinks()).hasSize(2);
        // self링크와 add링크가 포함돼어 있는 지 확인한다
        assertThat(items.hasLink(IanaLinkRelations.SELF)).isTrue();
        assertThat(items.hasLink("add")).isTrue();

        // 첫 번째 Item의 EntityModel 획득
        EntityModel<Item> first = items.getContent().iterator().next();

        // 첫 번째 Item의 EntityModel에서 SELF 링크를 통해 첫 번째 Item 정보 획득
        EntityModel<Item> item = this.webTestClient.get()
                // 첫번째 EntityModel<Item>을 가져와서 self 링크를 알아내고 다시 self 링크로 GET 요청을 보낸다
                .uri(first.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .exchange()
                // Item 세부정보를 가져온다
                .expectBody(new EntityModelType<Item>() {})
                .returnResult().getResponseBody();

        assertThat(item.getLinks()).hasSize(3);
        assertThat(item.hasLink(IanaLinkRelations.SELF)).isTrue();
        assertThat(item.hasLink(IanaLinkRelations.ITEM)).isTrue();
        // delete 링크가 포함돼있는지 확인
        assertThat(item.hasLink("delete")).isTrue();
    }
}
