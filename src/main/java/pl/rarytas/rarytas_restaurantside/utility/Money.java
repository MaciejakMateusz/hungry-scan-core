package pl.rarytas.rarytas_restaurantside.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Simple wrapper for BigDecimal with scale set to 2 and RoundingMode.HALF_UP
 **/
public class Money {
    public static BigDecimal of(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}