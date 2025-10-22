package com.hackybear.hungry_scan_core.utility;

public class Fields {

    //ROLES
    public static final String STAFF = "STAFF";
    public static final String MANAGER = "MANAGER";
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String CUSTOMER_READONLY = "CUSTOMER_READONLY";

    public static final String ROLES_EXCEPT_CUSTOMER =
            "hasAnyRole('" + STAFF + "', '" + MANAGER + "', '" + ADMIN + "')";

    public static final String ROLES_EXCEPT_READONLY_CUSTOMER =
            "hasAnyRole('" + STAFF + "', '" + MANAGER + "', '" + ADMIN + "', '" + CUSTOMER + "')";

    //REDIS CACHE
    public static final String USER_ID = "user.id";
    public static final String USERS_ALL = "users.all";

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

    public static final String INGREDIENTS_ALL = "ingredients.all";
    public static final String INGREDIENTS_PAGES = "ingredients.pages";
    public static final String INGREDIENTS_AVAILABLE = "ingredients.available";
    public static final String INGREDIENT_ID = "ingredient.id";

    public static final String VARIANT_ID = "variant.id";
    public static final String VARIANTS_ALL = "variants.all";

    public static final String STATS_SCANS_YEARLY = "stats.scans.yearly";
    public static final String STATS_SCANS_MONTHLY = "stats.scans.monthly";
    public static final String STATS_SCANS_WEEKLY = "stats.scans.weekly";
    public static final String STATS_SCANS_DAILY = "stats.scans.daily";
    public static final String STATS_MENU_ITEMS_YEARLY = "stats.menuItems.yearly";
    public static final String STATS_MENU_ITEMS_MONTHLY = "stats.menuItems.monthly";
    public static final String STATS_MENU_ITEMS_WEEKLY = "stats.menuItems.weekly";
    public static final String STATS_MENU_ITEMS_DAILY = "stats.menuItems.daily";
}