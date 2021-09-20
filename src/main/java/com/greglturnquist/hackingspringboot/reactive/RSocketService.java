package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.*;

/**
 * Created by KMS on 2021/09/20.
 */
@Controller
public class RSocketService {

    private final ItemRepository repository;
    // EmitterProcessor는 단지 Flux를 상속받은 특별한 버전의 Flux
    private final EmitterProcessor<Item> itemProcessor;
    private final FluxSink<Item> itemSink;
    //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
    // private final Sinks.Many<Item> itemsSink;

    public RSocketService(ItemRepository repository) {
        this.repository = repository; // <2>
        // EmitterProcessor.create()로 새 프로세스 생성
        this.itemProcessor = EmitterProcessor.create(); // <1>
        // EmitterProcessor에 새 Item을 추가하려면 진입점이 필요하며 이를 싱크(sink)라고 한다
        // sink() 메소드를 호출해서 싱크를 얻을 수 있다
        this.itemSink = this.itemProcessor.sink(); // <2>
        //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
        // this.itemsSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * 요청 응답 R소켓 익스체인지 처리
     * @param item
     * @return
     */
    // 도착지로 지정된 R소켓 메시지를 이 메소드로 라우팅
    @MessageMapping("newItems.request-response")
    // 스프링 메시징은 메시지가 들어오기를 리액티브하게 기다리고 있다고 메시지가 들어오면 메시지 본문을 인자로해서 save() 메소드를 호출한다
    // 반환 타입은 도메인 객체를 Item을 포함하는 리액터 타입이며, 이는 요청하는 쪽에서 예상하는 응답 메시지 시그니처와 일치한다
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) {
        // Item 객체에 대한 정보를 담고 있는 메시지를 받았으므로 비즈니스 로직을 수행할 차례다
        // 리액티브 리포지토리를 통해 Item 객체를 몽고디비에 저장한다
        return this.repository.save(item)
                // doOnNext()를 호출해서 새로 저장된 Item 객체를 가져와서 싱크를 통해 FluxProcessor로 내보낸다
                .doOnNext(this.itemSink::next);
        //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
        //         .doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem));
    }

    /**
     * 요청-스트림 R소켓 익스체인지
     * @return
     */
    @MessageMapping("newItems.request-stream")
    // 메시지가 들어오면 Item 목록을 조회한 후 Flux에 담아 반환한다
    public Flux<Item> findItemsViaRSocketRequestStream() {
        // 몽고디비에 저장된 Item 목록을 조회한 후 Flux에 담아 반환한다
        return this.repository.findAll()
                // doOnNext()를 호출해서 조회한 Item 객체를 싱크를 통해 FluxProcessor로 내보낸다
                .doOnNext(this.itemSink::next); // <4>
        //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
        //         .doOnNext(this.itemsSink::tryEmitNext);
    }

    /**
     * 실행 후 망각 R소켓 익스체인지
     * 요청-스트림과 라우트, 반환타입만 다르다
     * @param item
     * @return
     */
    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
        return this.repository.save(item) //
                .doOnNext(this.itemSink::next) //
                //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
                // .doOnNext(savedItem -> this.itemsSink.tryEmitNext(savedItem))
                .then();
    }

    /**
     * R소켓 익스체인지 채널 모니터링
     * @return
     */
    @MessageMapping("newItems.monitor")
    // 예제에서는 요청으로 들어오는 데이터가 없지만, 클라이언트가 요청에 데이터를 담아 보낼 수도 있다
    // 쿼리나 필터링처럼 클라이언트가 원하는 것을 요청 데이터에 담아 보낼 수도 있다
    // 그래서 반환 타입은 다른 방식에서 처럼 Mono가 아니라 복수의 Item 객체를 포함하는 Flux다
    public Flux<Item> monitorNewItems() {
        // 실제 반환되는 것은 단순히 EmitterProcessor다
        // EmitterProcessor도 Flux이므로 반환 타입에 맞는다
        // EmitterProcessor에는 입수, 저장, 발행된 Item 객체들이 들어 있다
        // 이 메소드를 구독하는 여러 주체들은 모두 EmitterProcessor에 담겨 있는 Item 객체들의 복사본을 받게 된다
        return this.itemProcessor;
        //  Deprecated인 FluxProcessor, EmitterProcessor의 대체 구현
        // return this.itemsSink.asFlux();
    }
}
