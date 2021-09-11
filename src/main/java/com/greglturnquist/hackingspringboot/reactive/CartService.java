package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 장바구니 서비스
 * Created by KMS on 2021/09/11.
 */
@Service
public class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    CartService(ItemRepository itemRepository, // <2>
                CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * 장바구니 상품 담기
     * @param cartId
     * @param id
     * @return
     */
    Mono<Cart> addToCart(String cartId, String id) { // <3>
        return this.cartRepository.findById(cartId) //
                .defaultIfEmpty(new Cart(cartId)) //
                .flatMap(cart -> cart.getCartItems().stream() //
                        .filter(cartItem -> cartItem.getItem().getId().equals(id)) //
                        .findAny() //
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        }).orElseGet(() -> this.itemRepository.findById(id) //
                                .map(CartItem::new) // <4>
                                .doOnNext(cartItem -> cart.getCartItems().add(cartItem)) //
                                .map(cartItem -> cart)))
                .flatMap(this.cartRepository::save); // <5>
    }
}