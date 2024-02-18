package pl.rarytas.rarytas_restaurantside.enums;

import lombok.Getter;

@Getter
public enum OpeningHours {
    OPENING(8),
    CLOSING(22);

    private final int intValue;

    OpeningHours(int intValue) {
        this.intValue = intValue;
    }
}
