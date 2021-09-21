package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import java.util.Date;
import java.util.Objects;

/**
 * 판매 상품 데이터
 * Created by KMS on 2021/09/11.
 */
public class Item {

    private @Id String id;
    private String name;
    private String description;
    private double price;

    private Item() {
    }

    Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    Item(String id, String name, String description, double price) {
        this(name, description, price);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Double.compare(item.price, price) == 0 &&
                Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
