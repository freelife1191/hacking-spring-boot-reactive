package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.*;

/**
 * Created by KMS on 2021/09/20.
 */
// @RestController가 붙은 클래스는 HTML을 렌더링하지 않는다
@RestController
public class RSocketController {
    // 스프링 프레임워크의 RSocketRequester를 리액터로 감싼 것이다
    // Mono를 사용하므로 R소켓에 연결된 코드는 새 클라이언트가 구독할 때마다 호출된다
    private final Mono<RSocketRequester> requester;
    // 스프링 부트는 RSocketRequesterAutoConfiguration 정책 안에서 자동설정으로 RSocketRequester.Builder 빈을 만들어준다
    // Jackson을 포함해서 여러 가지 인코더/디코더를 사용할 수 있으며 컨트롤러의 생성자를 통해 의존관계를 주입할 수 있다
    public RSocketController(RSocketRequester.Builder builder) {
        this.requester = builder
                //dataMimeType()을 통해 데이터의 미디어 타입을 지정한다
                // 여기에서는 application/json을 나타내는 스프링 상수를 사용한다
                .dataMimeType(APPLICATION_JSON)
                // 라우팅 정보 같은 메타데이터 값을 R소켓 표준인 essage/x.rsocket.routing.v0로 지정한다
                .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
                // TCP를 사용하므로 호스트 이름(hostname)과 포트 번호를 지정하고 connectTcp()를 호출해서 7000번 포트를 사용하는 R소켓 서버에 연결한다
                .connectTcp("localhost", 7000)
                // 견고성(robustness)을 높이기 위해 메시지 처리 실패 시 Mono가 5번까지 재시도할 수 있도록 지정한다
                .retry(5)
                // 요청 Mono를 핫 소스(hot source)로 전환한다
                // 핫 소스에서는 가장 최근의 신호는 캐시돼 있을 수도 있으며 구독자는 사본을 가지고 있을 수도 있다
                // 이 방식은 다수의 클라이언트가 동일한 하나의 데이터를 요구할 때 효율성을 높일 수 있다
                .cache();
    }

    /**
     * 웹플럭스 요청을 R소켓 요청-응답으로 전환
     * 요청-응답 방식 R소켓에서 새 Item 추가 전송
     * @param item
     * @return
     */
    @PostMapping("/items/request-response") // <1>
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        return this.requester //
                .flatMap(rSocketRequester -> rSocketRequester
                        // Mono<RSocketRequester>에 flatMap()을 적용해서 이 요청을 newItems.request-response로 라우팅할 수 있다
                        .route("newItems.request-response")
                        // Item 객체 정보를 data() 메소드에 전달한다
                        .data(item)
                        // 마지막으로 retrieveMono(Item.class)를 호출해서 Mono<Item> 응답을 원한다는 신호를 보낸다
                        .retrieveMono(Item.class))
                // 한 개의 Item이 반환되면 map()과 ResponseEntity 헬퍼 메소드를 사용해서 HTTP 201 Created 응답을 반환한다
                .map(savedItem -> ResponseEntity.created( // <5>
                        URI.create("/items/request-response")).body(savedItem));
    }

    /**
     * 웹플럭스 요청을 R소켓 요청-스트림으로 전환
     * Item 목록 조회 요청을 요청-스트림 방식의 R소켓 서버에 전달
     * @return
     */
    // Flux를 통해 JSON 스트림 데이터를 반환한다
    // 스트림 방식으로 반환하기 위해 미디어타입을 APPLICATION_NDJSON_VALUE로 지정한다
    // APPLICATION_NDJSON_VALUE의 실젯값은 "application/x-ndjson"이다
    @GetMapping(value = "/items/request-stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<Item> findItemsUsingRSocketRequestStream() {
        return this.requester
                // 여러 건의 조회 결과를 Flux에 담아 반환할 수 있도록 flatMapMany()를 적용한다
                .flatMapMany(rSocketRequester -> rSocketRequester
                        // Item 목록 요청을 R소켓 서버의 newItems.request-stream으로 라우팅한다
                        .route("newItems.request-stream")
                        // 여러 건의 Item을 Flux<Item>에 담아 반환하도록 요청한다
                        .retrieveFlux(Item.class)
                        // 여러 건의 Item을 1초에 1건씩 반환하도록 요청한다
                        // 여러 건의 데이터가 세 번의 응답되는 게 아니라 스트림을 통해 응답된느 것을 눈으로 쉽게 확인할 수 있도록
                        // 일부로 넣은 코드일 뿐이며 반드시 필요한 로직은 아니다
                        .delayElements(Duration.ofSeconds(1)));
    }

    /**
     * 웹플럭스 요청을 R소켓 실행 후 망각으로 전환
     * @param item
     * @return
     */
    @PostMapping("/items/fire-and-forget")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
        return this.requester
                .flatMap(rSocketRequester -> rSocketRequester
                        // 이 컨트롤러 메소드는 새 Item 생성 요청을 받아서 R소켓 서버의 newItems.fire-and-forget 경로로 전달한다
                        .route("newItems.fire-and-forget")
                        .data(item)
                        // 앞의 요청-응답 예제에서는 retrieveMono()를 호출해서 새 Item 정보가 포함돼 있는 Mono를 받아왔지만
                        // 이번 실행 후 망각 예제는 send()를 호출하고 Mono<Void>를 반환받는다
                        .send())
                // 앞의 요청-응답 예제에서는 새 Item 정보가 포함된 Mono를 map()을 사용해서 Item 정보를 포함하는 ResponseEntity()를 Mono로 변환해서 반환했지만
                // 실행 후 망각 예제에서는 Mono<Void>를 반환받았으므로 map()을 한다 해도 아무 일도 일어나지 않는다
                // 그래서 새로 생성된 Item에 대한 Location 헤더값을 포함하는 HTTP 201 Created를 반환하려면
                // map()이 아니라 then()과 Mono.just()를 사용해 Mono를 새로 만들어서 반환해야 한다
                .then(
                        Mono.just(
                                ResponseEntity.created(
                                        URI.create("/items/fire-and-forget")).build()));
    }

    /**
     * 웹플럭스 요청을 R소켓 채널로 전환
     * @return
     */
    // produces = TEXT_EVENT_STREAM_VALUE는 응답할 결과가 생길 때마다 결괏값을 스트림에 흘려보낸다는 것을 의미한다
    // 참고로 cURL도 스트림 결과를 받을 수 있다
    @GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Item> liveUpdates() {
        return this.requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        // 채널 방식으로 요청을 처리하는 newItems.monitor 경로로 라우팅한다
                        .route("newItems.monitor")
                        // 결과 필터링에 필요한 데이터를 data()를 통해 전달할 수도 있지만
                        // 이번 예제에서는 사용하지 않았다
                        // retrieveFlux(Item.class)를 호출해서 실제 Flux<Item> 결과 데이터를 필요로 한다는 것을 알려준다
                        .retrieveFlux(Item.class));
    }
}
