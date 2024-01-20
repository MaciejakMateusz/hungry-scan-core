package pl.rarytas.rarytas_restaurantside.utility;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
    CASH("cash"),
    CARD("card"),
    ONLINE("online");

    private final String methodName;

    PaymentMethodEnum(String methodName) {
        this.methodName = methodName;
    }
}
