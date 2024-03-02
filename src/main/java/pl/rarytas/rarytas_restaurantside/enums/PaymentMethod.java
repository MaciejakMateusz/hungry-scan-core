package pl.rarytas.rarytas_restaurantside.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("cash"),
    CARD("card"),
    ONLINE("online");

    private final String methodName;

    PaymentMethod(String methodName) {
        this.methodName = methodName;
    }
}
