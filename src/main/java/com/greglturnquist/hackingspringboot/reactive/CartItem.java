package com.greglturnquist.hackingspringboot.reactive;

import java.util.Objects;

/**
 * 아이템과 수량을 포함하는 구매 상품 데이터
 * Created by KMS on 2021/09/11.
 */
public class CartItem {

    private Item item;
    private int quantity;

    private CartItem() {}

    CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }
    // end::code[]

    public void increment() {
        this.quantity++;
    }

    public void decrement() {
        this.quantity--;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity && Objects.equals(item, cartItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity);
    }

    @Override
    public String toString() {
        return "CartItem{" + "item=" + item + ", quantity=" + quantity + '}';
    }

}
