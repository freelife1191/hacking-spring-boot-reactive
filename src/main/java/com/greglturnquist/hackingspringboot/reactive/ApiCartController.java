package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by KMS on 2021/09/20.
 */
@RestController
public class ApiCartController {

    private final InventoryService service;

    public ApiCartController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/api/carts")
    Flux<Cart> findAll() {
        return this.service.getAllCarts()
                .switchIfEmpty(this.service.newCart());
    }

    @GetMapping("/api/carts/{id}")
    Mono<Cart> findOne(@PathVariable String id) {
        return this.service.getCart(id);
    }

    @PostMapping("/api/carts/{cartId}/add/{itemId}")
    Mono<Cart> addToCart(@PathVariable String cartId, @PathVariable String itemId) {
        return this.service.addItemToCart(cartId, itemId);
    }

    @DeleteMapping("/api/carts/{cartId}/remove/{itemId}")
    Mono<Cart> removeFromCart(@PathVariable String cartId, @PathVariable String itemId) {
        return this.service.removeOneFromCart(cartId, itemId);
    }
}
