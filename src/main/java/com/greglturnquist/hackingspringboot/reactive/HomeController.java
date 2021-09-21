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

    @GetMapping
    Mono<Rendering> home(Authentication auth) { // <1>
        return Mono.just(Rendering.view("home.html") //
                .modelAttribute("items", this.inventoryService.getInventory()) //
                .modelAttribute("cart", this.inventoryService.getCart(cartName(auth)) // <2>
                        .defaultIfEmpty(new Cart(cartName(auth)))) //
                .modelAttribute("auth", auth) // <3>
                .build());
    }

    @PostMapping("/add/{id}")
    Mono<String> addToCart(Authentication auth, @PathVariable String id) {
        return this.inventoryService.addItemToCart(cartName(auth), id) //
                .thenReturn("redirect:/");
    }

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