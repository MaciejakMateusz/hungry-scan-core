package pl.rarytas.rarytas_restaurantside.utility;


import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeHelper {

    public static boolean isInTimeRange(LocalTime candidateTime, LocalTime existingTime, LocalTime existingExpirationTime) {
        return candidateTime.isAfter(existingTime) && candidateTime.isBefore(existingExpirationTime);
    }

    public static boolean isNotInPast(LocalDateTime localDateTime) {
        return localDateTime.isAfter(LocalDateTime.now());
    }

    public static boolean timesIntersect(LocalTime candidateExpirationTime, LocalTime existingTime, LocalTime existingExpirationTime) {
        return candidateExpirationTime.isBefore(existingExpirationTime) && candidateExpirationTime.isAfter(existingTime);
    }
}