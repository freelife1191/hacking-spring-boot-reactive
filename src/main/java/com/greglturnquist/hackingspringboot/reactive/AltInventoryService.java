package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * 리액터 패러다임에 익숙하지 않은 한팀의 동료가 구현
 * Created by KMS on 2021/09/12.
 */
// tag::code[]
@Service
class AltInventoryService {

    private ItemRepository itemRepository;

    private CartRepository cartRepository;

    AltInventoryService(ItemRepository repository, CartRepository cartRepository) {
        this.itemRepository = repository;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> getCart(String cartId) {
        return this.cartRepository.findById(cartId);
    }

    public Flux<Item> getInventory() {
        return this.itemRepository.findAll();
    }

    Mono<Item> saveItem(Item newItem) {
        return this.itemRepository.save(newItem);
    }

    Mono<Void> deleteItem(String id) {
        return this.itemRepository.deleteById(id);
    }

    // tag::blocking[]
    Mono<Cart> addItemToCart(String cartId, String itemId) {
        Cart myCart = this.cartRepository.findById(cartId) //
                .defaultIfEmpty(new Cart(cartId)) //
                .block(); // 블로킹 코드 호출

        return myCart.getCartItems().stream() //
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId)) //
                .findAny() //
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(myCart);
                }) //
                .orElseGet(() -> this.itemRepository.findById(itemId) //
                        .map(item -> new CartItem(item)) //
                        .map(cartItem -> {
                            myCart.getCartItems().add(cartItem);
                            return myCart;
                        })) //
                .flatMap(cart -> this.cartRepository.save(cart));
    }
    // end::blocking[]

    Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId) //
                .defaultIfEmpty(new Cart(cartId)) //
                .flatMap(cart -> cart.getCartItems().stream() //
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId)) //
                        .findAny() //
                        .map(cartItem -> {
                            cartItem.decrement();
                            return Mono.just(cart);
                        }) //
                        .orElse(Mono.empty())) //
                .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream() //
                        .filter(cartItem -> cartItem.getQuantity() > 0) //
                        .collect(Collectors.toList()))) //
                .flatMap(cart -> this.cartRepository.save(cart));
    }
}
// end::code[]