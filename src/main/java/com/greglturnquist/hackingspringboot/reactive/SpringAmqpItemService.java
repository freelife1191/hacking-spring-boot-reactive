package com.greglturnquist.hackingspringboot.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 리액티브 방식으로 AMQP 메시지 사용
 * Created by KMS on 2021/09/20.
 */
@Service
public class SpringAmqpItemService {

    private static final Logger log = LoggerFactory.getLogger(SpringAmqpItemService.class);

    private final ItemRepository repository;

    public SpringAmqpItemService(ItemRepository repository) {
        this.repository = repository;
    }

    // @RabbitListener가 붙은 메소드는 스프링 AMQP 메시지 리스너로 등록되어 메시지를 소비할 수 있다
    @RabbitListener(
            ackMode = "MANUAL",
            // @QueueBinding은 큐를 익스체인지에 바인딩하는 방법을 지정한다
            bindings = @QueueBinding(
                    // @Queue는 임의 지속성 없는 익명 큐를 생성한다
                    // 특정 큐를 바인딩하려면 @Queue의 인자로 큐의 이름을 지정한다
                    // durable, exclusive, autoDelete 같은 속성값도 설정할 수도 있다
                    value = @Queue,
                    // @Exchange는 이 큐와 연결될 exchange를 지정한다
                    // 예제에서는 hacking-spring-boot exchange를 큐와 연결한다
                    // exchange의 다른 속성값을 설정할 수도 있다
                    exchange = @Exchange("hacking-spring-boot"),
                    // key는 라우팅 키를 지정한다
                    key = "new-items-spring-amqp"))
    // @RabbitListener에서 지정한 내용에 맞는 메시지가 들어오면 실행되며
    // 메시지에 들어 있는 Item 데이터는 item 변수를 통해 전달된다
    public Mono<Void> processNewItemsViaSpringAmqp(Item item) {
        log.debug("Consuming => " + item);
        // Item 객체가 몽고디비에 저장된다
        // 반환 타입이 리액터 타입인 Mono이므로 then()을 호출해서 저장이 완료될 때까지 기다린다
        // 스프링 AMQP는 리액터 타입도 처리할 수 있으므로 구독도 스프링 AMQP에게 위임할 수 있다
        return this.repository.save(item).then();
    }
}
