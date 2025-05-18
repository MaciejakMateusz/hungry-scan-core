package com.hackybear.hungry_scan_core.utility;

public class Fields {

    //ROLES
    public static final String WAITER = "WAITER";
    public static final String COOK = "COOK";
    public static final String MANAGER = "MANAGER";
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

    public static final String ROLES_EXCEPT_CUSTOMER =
            "hasAnyRole('" + WAITER + "', '" + COOK + "', '" + MANAGER + "', '" + ADMIN + "')";

    public static final String ROLES_EXCEPT_READONLY_CUSTOMER =
            "hasAnyRole('" + WAITER + "', '" + COOK + "', '" + MANAGER + "', '" + ADMIN + "', '" + CUSTOMER + "')";

    //REDIS CACHE
    public static final String USER_MENU_ID = "user.activeMenuId";
    public static final String USER_MENU = "user.activeMenu";
    public static final String USER_RESTAURANT_ID = "user.activeRestaurantId";
    public static final String USER_RESTAURANT = "user.activeRestaurant";

    public static final String RESTAURANT_ID = "restaurant.id";
    public static final String RESTAURANT_TOKEN = "restaurant.token";
    public static final String RESTAURANTS_ALL = "restaurants.all";

    public static final String MENU_ID = "menu.id";
    public static final String MENU_CUSTOMER_ID = "menu.customer.id";
    public static final String MENUS_ALL = "menus.all";

    public static final String CATEGORIES_ALL = "categories.all";
    public static final String CATEGORIES_AVAILABLE = "categories.available";
    public static final String CATEGORIES_DISPLAY_ORDERS = "categories.displayOrders";
    public static final String CATEGORIES_COUNT = "categories.count";
    public static final String CATEGORY_ID = "category.id";

    public static final String VARIANT_ID = "variant.id";
    public static final String VARIANTS_ALL = "variants.all";
}