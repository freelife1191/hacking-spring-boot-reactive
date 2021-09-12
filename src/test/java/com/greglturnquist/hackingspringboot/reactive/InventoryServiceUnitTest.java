package com.greglturnquist.hackingspringboot.reactive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by KMS on 2021/09/12.
 */
// tag::extend[]
@ExtendWith(SpringExtension.class) // <1> 스프링에 특화된 테스트 기능을 사용할 수 있게 해준다
class InventoryServiceUnitTest { // <2>
    // end::extend[]

    // tag::class-under-test[]
    // 테스트 대상 클래스 아무런 애너테이션도 붙지 않으며 테스트할 때 초기화된다
    InventoryService inventoryService; // <1>

    // 테스트 대상이 아니므로 가짜 객체를 만들고 @MockBean 애너테이션을 붙여 스프링 빈으로 등록한다
    // @MockBean 애너테이션을 보면 Mockito를 사용해서 가짜 객체를 만들고 이를 애플리케이션 컨텍스트에 빈으로 추가한다
    @MockBean private ItemRepository itemRepository; // <2>

    @MockBean private CartRepository cartRepository; // <2>
    // end::class-under-test[]

    // tag::before[]
    @BeforeEach // 모든 테스트 메소드 실행 전에 테스트 준비 내용을 담고 있음
    // <1>
    void setUp() {
        // 테스트 데이터 정의 <2>
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // 협력자와의 상호작용 정의
        // by your collaborators <3>
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        // 가짜 협력자를 생성자에 주입하면서 테스트 대상 클래스 생성
        inventoryService = new InventoryService(itemRepository, cartRepository); // <4>
    }
    // end::before[]

    /**
     * 실제 테스트 코드
     */
    // tag::test[]
    @Test
    void addItemToEmptyCartShouldProduceOneCartItem() { // <1>
        inventoryService.addItemToCart("My Cart", "item1") // <2>
                // 테스트 대상 메소드의 반환 타입인 Mono<Cart>를 리액터 테스트 모듈의 정적 메소드인 StepVerifier.create()에 메소드 레퍼런스로 연결해서
                // 테스트 기능을 전담하는 리액터 타입 핸들러를 생성한다
                .as(StepVerifier::create) // <3> 결괏값을 얻기 위해 블로킹 방식으로 기다리는 대신에 리액터의 테스트 도구가 대신 구독하고 값을 확인
                // 값을 확인할 수 있는 적절한 함수 expectNextMatches
                .expectNextMatches(cart -> { // <4> 함수와 람다식을 사용해서 결과를 검증
                    // AssertJ를 사용해서 각 장바구니에 담긴 상품의 개수를 추출하고 장바구니에 한 가지 종류의 상품 한개만 들어 있음을 단언한다
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
                            .containsExactlyInAnyOrder(1); // <5>
                    // 각 장바구니에 담긴 상품을 추출해서 한 개의 상품만 있음을 검증하고 그 상품이 setUp() 메소드에서 정의한 데이터와 맞는지 검증한다
                    assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>
                    // expectNextMatche() 메소드는 boolean을 반환해야 하므로 이 지점까지 통과했다면 true를 반환한다
                    return true; // <7>
                }) //
                // 마지막 단언(assertion)은 리액티브 스트림의 complete 시그널이 발생하고 리액터 플로우가 성공적으로 완료됐음을 검증한다
                .verifyComplete(); // <8> onComplete 시그널을 확인
    }
    // end::test[]

    /**
     * 탑 레벨과는 다른 방식으로 작성한 테스트 코드
     * 이 방식은 단순히 바깥에 명시적으로 드러난 행이 아니라 메소드의 인자까지
     * 뒤져봐야 무엇이 테스트되는지를 알 수 있으므로 별로 좋아보이지 않음
     */
    // tag::test2[]
    @Test
    void alternativeWayToTest() { // <1>
        StepVerifier.create( //
                inventoryService.addItemToCart("My Cart", "item1")) //
                .expectNextMatches(cart -> { // <4>
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
                            .containsExactlyInAnyOrder(1); // <5>

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>

                    return true; // <7>
                }) //
                .verifyComplete(); // <8>
    }
    // end::test2[]

}