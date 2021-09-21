package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Created by KMS on 2021/09/12.
 */
@Service
public class InventoryService {
    private ItemRepository itemRepository;

    private CartRepository cartRepository;

    InventoryService(ItemRepository repository, //
                     CartRepository cartRepository) {
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

    /**
     * 리액터 로깅 적용
     * @param cartId
     * @param itemId
     * @return
     */
    Mono<Cart> addItemToCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId)) //
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny() //
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        }) //
                        .orElseGet(() -> this.itemRepository.findById(itemId) //
                                .map(item -> new CartItem(item)) //
                                .map(cartItem -> {
                                    cart.getCartItems().add(cartItem);
                                    return cart;
                                })))
                .flatMap(cart -> this.cartRepository.save(cart));
    }

    Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.decrement();
                            return Mono.just(cart);
                        }) //
                        .orElse(Mono.empty()))
                .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getQuantity() > 0)
                        .collect(Collectors.toList())))
                .flatMap(cart -> this.cartRepository.save(cart));
    }
}
