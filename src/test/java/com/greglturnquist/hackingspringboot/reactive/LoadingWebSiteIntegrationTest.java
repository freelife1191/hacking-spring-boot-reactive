package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_HTML;

/**
 * 실제 웹 컨테이너를 사용하는 테스트 케이스
 * Created by KMS on 2021/09/12.
 */
// tag::code[]
@Disabled("blockhound-junit-platform 의존 관계를 제거한 후에 실행해야 성공한다.")
// @SpringBootApplication이 붙은 클래스를 찾아서 내장 컨테이너를 실행함
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //<1> 임의의 포트에 내장 컨테이너를 바인딩
@AutoConfigureWebTestClient // <2>애플리케이션에 요청을 날리는 WebTestClient 인스턴스를 생성한다
class LoadingWebSiteIntegrationTest {

    @Autowired // 테스트 케이스에 주입
    WebTestClient client; // <3>

    @Test
        // <4>
    void test() {
        client.get().uri("/").exchange() // 홈 컨트롤러의 루트 경로를 호출
                .expectStatus().isOk() // HTTP 응답코드 검증
                .expectHeader().contentType(TEXT_HTML) //헤더 검증
                .expectBody(String.class) //
                .consumeWith(exchangeResult -> { //응답 본문 값 검증 수행
                    assertThat(exchangeResult.getResponseBody()).contains("<a href=\"/add");
                });
    }
}
// end::code[]

