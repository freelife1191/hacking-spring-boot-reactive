package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RabbitMQ 테스트컨테이너를 사용하는 테스트
 * Created by KMS on 2021/09/20.
 */
// 자동설정, 환경설정 값 읽기, 내장 웹 컨테이너 등 테스트를 위한 애플리케이션 구동에 필요한 모든 것을 활성화한다
// 기본적으로 실제 운영환경이 아니라 실제 운영환경을 흉내 낸 mock 환경을 사용한다
@SpringBootTest
// WebTestClient를 자동설정한다
@AutoConfigureWebTestClient
// Junit 5에서 제공하는 애너테이션이며 테스트컨테이너를 테스트에 사용할 수 있게 해준다
@Testcontainers
// 지정한 클래스를 테스트 실행 전에 먼저 애플리케이션 컨텍스트에 로딩해준다
@ContextConfiguration
public class RabbitTest {
    // 테스트에 사용할 RabbitMQContainer를 생성한다
    // RabbitMQContainer는 테스트에 사용할 RabbitMQ 인스턴스를 관리한다
    @Container static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.9.5-management-alpine");
    // 테스트에 사용할 WebTestClient를 주입받는다
    @Autowired WebTestClient webTestClient;
    // 요청 처리 결과 확인에 사용할 ItemRepository를 주입받는다
    @Autowired ItemRepository repository;
    // 자바 8의 함수형 인터페이스인 Supplier를 사용해서 환경설정 내용을 Environment에 동적으로 추가한다
    // container::getContainerIpAddress와 container::getAmqpPort 메소드 핸들을 사용해서
    // 테스트컨테이너에서 실행한 RabbitMQ Broker의 호스트이름과 포트 번호를 가져온다
    // 이렇게 하면 RabbitMQ 연결 세부정보를 테스트컨테이너에서 읽어와서 스프링 AMQP에서 사용할 수 있도록 스프링 부트 환경설정 정보에 저장한다
    @DynamicPropertySource // <8>
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
    }

    /**
     * AMQP 메시징 테스트
     * @throws InterruptedException
     */
    @Test
    void verifyMessagingThroughAmqp() throws InterruptedException {
        this.webTestClient.post().uri("/items") // <1>
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99)) //
                .exchange() //
                .expectStatus().isCreated() //
                .expectBody();

        // 1500밀리초 동안 sleep() 처리해서 해당 메시지가 브로커를 거쳐 데이터 저장소에 저장될 때까지 기다린다
        // 이렇게 해서 테스트에 사용되는 메시지 처리 순서를 맞출 수 있다
        Thread.sleep(1500L);

        this.webTestClient.post().uri("/items") // <3>
                .bodyValue(new Item("Smurf TV tray", "nothing important", 29.99)) //
                .exchange() //
                .expectStatus().isCreated() //
                .expectBody();

        // 메시지가 처리될 수 있도록 2000밀리초 동안 sleep() 한다
        Thread.sleep(2000L); // <4>
        // ItemRepository를 사용해서 몽고디비에 쿼리를 날려서 2개의 Item 객체가 저장됐는지 확인한다
        this.repository.findAll() // <5>
                .as(StepVerifier::create) //
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99);
                    return true;
                }) //
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("Smurf TV tray");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(29.99);
                    return true;
                }) //
                .verifyComplete();
    }
}
