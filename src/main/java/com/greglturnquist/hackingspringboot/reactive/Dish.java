package com.greglturnquist.hackingspringboot.reactive;

/**
 * 요리(Dish) 도메인 객체 정의
 * Created by KMS on 2021/09/11.
 */
public class Dish {

    private String description;
    private boolean delivered = false;

    public static Dish deliver(Dish dish) {
        Dish deliveredDish = new Dish(dish.description);
        deliveredDish.delivered = true;
        return deliveredDish;
    }

    Dish(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "description='" + description + '\'' +
                ", delivered=" + delivered +
                '}';
    }
}
