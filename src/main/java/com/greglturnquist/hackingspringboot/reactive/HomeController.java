package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * 사이트 초기 홈 화면을 보여주는 단순한 홈 컨트롤러
 * Created by KMS on 2021/09/11.
 */
@Controller
public class HomeController {

    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private CartService cartService;
    private InventoryService inventoryService;

    public HomeController(ItemRepository itemRepository, CartRepository cartRepository,
                          CartService cartService, InventoryService inventoryService) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.inventoryService = inventoryService;
    }

    /**
     * 루트 URL에서 장바구니를 보여주도록 개선
     * @return
     */
    @GetMapping
    Mono<Rendering> home() {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.itemRepository.findAll())
                .modelAttribute("cart", this.cartRepository.findById("My Cart")
                    .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }

    /**
     * 장바구니에 상품 추가
     * @param id
     * @return
     */
    @PostMapping("/add/{id}")
    Mono<String> addToCart(@PathVariable String id) {
        // 상품 담기 기능을 CartService에게 위임해서 간결해진 addToCart() 메소드
        return this.cartService.addToCart("My Cart", id)
                .thenReturn("redirect:/");
        /*
        return this.cartRepository.findById("My Cart")
                .defaultIfEmpty(new Cart("My Cart"))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem()
                                .getId().equals(id)) //CartItem을 순회하며 새로 장바구니에 담은 것과 동일한 종류의 상품이 이미 있는지 확인할 수 있음
                        .findAny() // Optional<CartItem>을 반환
                        // 같은 상품이 있다면 map() 내부에서 해당 상품의 수량만 증가시키고 장바구니를 Mono에 담아 반환한다
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        })
                        // 새로 장바구니에 담은 상품이 장바구니에 담겨 있지 않은 상품이라면 몽고디비에서 해당상품을 조회한후
                        .orElseGet(() -> this.itemRepository.findById(id)
                                //수량을 1로 지정하고 CartItem에 담은 다음
                                .map(CartItem::new)
                                .map(cartItem -> {
                                    // CartItem을 장바구니에 추가한 후에 장바구니를 반환
                                    cart.getCartItems().add(cartItem);
                                    return cart;
                                })))
                // 업데이트된 장바구니를 몽고디비에 저장
                .flatMap(cart -> this.cartRepository.save(cart)) //flatMap으로 지정하지 않으면 Mono<Mono<Cart>>가 반환된다
                // 웹플럭스가 HTTP 요청을 / 위치로 리다이렉트한다
                .thenReturn("redirect:/");
        */
    }

    /**
     * Example 쿼리로 구현된 서비스를 사용하는 웹 컨트롤러
     * @param name
     * @param description
     * @param useAnd
     * @return
     */
    // tag::search[]
    @GetMapping("/search") // <1>
    Mono<Rendering> search( //
                            @RequestParam(required = false) String name, // <2>
                            @RequestParam(required = false) String description, //
                            @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("home.html") // <3>Rendering 컨테이너를 사용해서 화면에 렌더링할 템플릿을 선언한다
                .modelAttribute("items", //
                        // 메소드를 호출한 검색 결과를 데이터 모델에 담는다
                        inventoryService.searchByExample(name, description, useAnd)) // <4>지연(lazy) 방식이라 구독을 해야 실제 검색이 수행된다
                .modelAttribute("cart", //
                        this.cartRepository.findById("My Cart")
                                .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }
    // end::search[]
}