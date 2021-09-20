package com.greglturnquist.hackingspringboot.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

/**
 * AMQP 메시징을 처리할 수 있는 리액티브 컨트롤러
 * Created by KMS on 2021/09/20.
 */
// JSON 형태로 데이터를 입력받고 JSON 형태로 출력한다
@RestController
public class SpringAmqpItemController {

    private static final Logger log = LoggerFactory.getLogger(SpringAmqpItemController.class);

    // spring-data-stater-amqp는 Spring AMQP를 classpath에 추가한다
    // 그래서 스프링 부트 자동설정을 통해 AmqpTemplate을 테스트에 사용할 수 있다
    // RabbitMQ를 사용하므로 실제 구현체로는 RabbitTemplate이 사용된다
    // 생성자를 통해 AmqpTemplate을 주입받아서 메시지를 전송할 때 사용한다
    private final AmqpTemplate template; // <2>

    public SpringAmqpItemController(AmqpTemplate template) {
        this.template = template;
    }

    /**
     * 리액티브 컨트롤러에서 AMQP 메시지 전송
     * @param item
     * @return
     */
    @PostMapping("/items") // <1>
    // @RequestBody 애너테이션은 스프링 WebFlux에서 요청 본문에서 데이터를 추출하도록 지시한다
    // 물론 구독이 발생해야 실제 추출도 실행된다
    Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item) {
        return item
                // AmqpTemplate은 블로킹 API를 호출하므로 subscribeOn()을 통해 바운디드 엘라스틱 스케줄러(bounded elastic scheduler)에서
                // 관리하는 별도의 스레드에서 실행되게 만든다
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(content -> Mono
                        // 람다식을 사용해서 AmqpTemplate 호출을 Callable로 감싸고 Mono.fromCallable()을 통해 Mono를 생성한다
                        .fromCallable(() -> {
                            // AmqpTemplate의 convertAndSend()를 호출해서 Item 데이터를 new-items-spring-amqp라는
                            // routing key와 함께 hacking-spring-boot exchange로 전송한다
                            this.template.convertAndSend(
                                    "hacking-spring-boot", "new-items-spring-amqp", content);
                            // 새로 생성되어 추가된 Item 객체에 대한 URI를 location 헤더에 담아 HTTP 201 Created 상태 코드와 함께 반환한다
                            return ResponseEntity.created(URI.create("/items")).build();
                        }));
    }

}
