package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 블록하운드 통합 테스트
 * Created by KMS on 2021/09/12.
 */
// tag::1[]
@ExtendWith(SpringExtension.class) // <1>
public class BlockHoundIntegrationTest {

    AltInventoryService inventoryService; // <2>

    @MockBean
    ItemRepository itemRepository; // <3>
    @MockBean CartRepository cartRepository;
    // end::1[]

    // tag::2[]
    @BeforeEach
    void setUp() {
        // Define test data <1> 테스트 데이터 정의

        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // Define mock interactions provided
        // by your collaborators <2>협력자와의 가짜 상호작용 정의

        /**
         * cartRepository.findById()는 Mono.empty()를 반환한다
         * Mono.empty()는 MonoEmtpy 클래스의 싱글턴 객체를 반환한다
         * 리액터는 이런 인스턴스를 감지하고 런타임에서 최적화한다
         * block() 호출이 없으므로 블록하운드는 아무것도 검출하지 않고 지나간다
         *
         * 이것은 리액터의 문제가 아니라 테스트 시나리오의 문제다
         * 개발자는 장바구니가 없을 때도 문제없이 처리하기를 바랐지만 리액터는 필요하지 않다면 블로킹 호출을 친절하게 알아서 삭제한다
         *
         * 테스트 관점에서 이처럼 블로킹 호출이 알아서 제거되는 문제를 해결하려면 MonoEmtpy를 숨겨서 리액터의 최적화 루틴한테 걸리지 않게 해야 한다
         * 리액터 자바독 문서에서는 이 연산을 정확하게 설명하고 있다
         * "Mono.hide()의 주목적은 진단을 정확하게 수행하기 위해 식별성 기준 최적화를 방지하는 것이다
         */
        when(cartRepository.findById(anyString())) //
                .thenReturn(Mono.<Cart> empty().hide()); // <3>비어 있는 결과를 리액터로부터 감춘다

        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new AltInventoryService(itemRepository, cartRepository);
    }
    // end::2[]

    // tag::3[]

    /**
     * 테스트 안에서 블로킹 코드 검출
     *
     * 테스트 메소드는 명시적인 블로킹 호출을 포함하고 있으므로 예외가 발생하고, 예외가 발생할 것을 예상하고
     * verifyErrorSatisfies()를 호출해서 발생한 예외의 메시지를 단언하는 테스트는 성공한다
     *
     * 일반적인 테스트 케이스는 블로킹 코드가 없다는 것을 검증하는 것이 목적이고, 실행 중 오류 없이 완료될 것을 예상하므로
     * verifyComplete()를 호출해야 한다
     * 블로킹 코드가 없음을 예상하는 상황에서 블로킹 코드 호출이 포함돼 있다면 테스트 케이스는 실패한다
     *
     * 명시적으로 블로킹 코드가 포함된 addItemToCart()를 호출하며 verifyComplete() 대신에
     * verifyErrorSatisfies()를 사용해서 예외가 발생하고 예외 메시지가 단언문을 통과해야 테스트가 통과한다
     *
     * 요점은 블록하운드를 테스트 케이스에서 사용할 수 있도록 설정해서 테스트 코드에서 발생하는 블로킹 호출을 잡아낸다는 점이다
     * 그리고 이런 검사 작업을 사용 코드에서 할 필요가 없다는 점도 중요하다
     */
    @Test
    void blockHoundShouldTrapBlockingCall() { //
        // 블로킹되지 않는다는 것을 블록하운드로 검증하려면 리액터 스레드 안에서 실행돼야 한다
        // Mono.delay()를 실행해서 후속 작업이 리액터 스레드 안에서 실행되게 만든다
        Mono.delay(Duration.ofSeconds(1)) // <1>
                // tick 이벤트가 발생하면 테스트할 메소드인 addItemToCart()를 실행한다
                .flatMap(tick -> inventoryService.addItemToCart("My Cart", "item1")) // <2>
                .as(StepVerifier::create) // <3> addItemToCart()가 반환하는 Mono를 리액터 StepVerifier로 전환한다
                .verifyErrorSatisfies(throwable -> { // <4> 블로킹 호출이 있으므로 예외가 발생하며, 이 예외를 단언문으로 검증한다
                    assertThat(throwable).hasMessageContaining( //
                            "block()/blockFirst()/blockLast() are blocking");
                });
    }
    // end::3[]
}
// end::code[]