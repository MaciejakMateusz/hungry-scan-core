package pl.rarytas.rarytas_restaurantside.utility;

public class Constants {
    public static final String WAITER = "WAITER";
    public static final String COOK = "COOK";
    public static final String MANAGER = "MANAGER";
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

    public static final String ROLES_EXCEPT_CUSTOMER =
            "hasAnyRole('" + WAITER + "', '" + COOK + "', '" + MANAGER + "', '" + ADMIN + "')";

    public static final String ROLES_EXCEPT_READONLY_CUSTOMER =
            "hasAnyRole('" + WAITER + "', '" + COOK + "', '" + MANAGER + "', '" + ADMIN + "', '" + CUSTOMER + "')";
}
