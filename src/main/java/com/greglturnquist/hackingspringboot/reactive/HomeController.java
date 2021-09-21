package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * 사이트 초기 홈 화면을 보여주는 단순한 홈 컨트롤러
 * 스프링 시큐리티 인증 적용
 * Created by KMS on 2021/09/11.
 */
@Controller
public class HomeController {

    private final InventoryService inventoryService;

    public HomeController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * 사용자별 장바구니 작성
     * @param auth
     * @return
     */
    @GetMapping
    // Authentication을 home() 메소드의 인자로 추가하면 스프링 시큐리티가 구독자 컨텍스트(subscriber context)에서
    // Authentication 정보를 추출해서 인자로 주입해준다
    Mono<Rendering> home(Authentication auth) {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.inventoryService.getInventory())
                // auth를 인자로 전달해서 cartName() 메소드를 호출하면 장바구니 id를 반환한다
                .modelAttribute("cart", this.inventoryService.getCart(cartName(auth))
                        .defaultIfEmpty(new Cart(cartName(auth))))
                // Authentication 객체를 템플릿에 모델로 제공해주면, 템플릿이 웹 페이지의 컨텍스트에
                // 모델 데이터를 담아서 사용할 수 있게 된다
                .modelAttribute("auth", auth)
                .build());
    }

    /**
     * Item 추가
     * @param auth
     * @param id
     * @return
     */
    @PostMapping("/add/{id}")
    Mono<String> addToCart(Authentication auth, @PathVariable String id) {
        return this.inventoryService.addItemToCart(cartName(auth), id) //
                .thenReturn("redirect:/");
    }

    /**
     * Item 삭제
     * @param auth
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{id}")
    Mono<String> removeFromCart(Authentication auth, @PathVariable String id) {
        return this.inventoryService.removeOneFromCart(cartName(auth), id) //
                .thenReturn("redirect:/");
    }

    @PostMapping
    @ResponseBody
    Mono<Item> createItem(@RequestBody Item newItem) {
        return this.inventoryService.saveItem(newItem);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    Mono<Void> deleteItem(@PathVariable String id) {
        return this.inventoryService.deleteItem(id);
    }

    private static String cartName(Authentication auth) {
        return auth.getName() + "'s Cart";
    }
}