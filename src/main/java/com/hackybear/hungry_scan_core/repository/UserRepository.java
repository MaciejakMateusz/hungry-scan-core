package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.restaurants r
            JOIN FETCH u.roles ro
            WHERE u.organizationId = :organizationId
            AND u.id != :currentUserId
            AND u.forename != 'temp'
            ORDER BY u.forename ASC
            """)
    Set<User> findAllByOrganizationId(@Param("organizationId") Long organizationId, @Param("currentUserId") Long currentUserId);

    @Query("""
            SELECT u FROM User u
            WHERE (
                lower(u.forename)  LIKE :filterValue
                OR lower(u.surname) LIKE :filterValue
                OR u.username LIKE :filterValue
            )
            AND u.organizationId = :organizationId
            AND u.username != :currentUsername
            ORDER BY u.forename ASC
            """)
    Set<User> filterUsers(@Param("filterValue") String filterValue,
                          @Param("organizationId") Long organizationId,
                          @Param("currentUsername") String currentUsername);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CUSTOMER' OR r.name = 'ROLE_CUSTOMER_READONLY'")
    List<User> findAllCustomers();

    Optional<User> findByUsername(String username);

    @Query("SELECT u.activeRestaurantId FROM User u WHERE u.username = :username")
    Optional<Long> getActiveRestaurantIdByUsername(@Param("username") String username);

    @Query("SELECT r FROM Restaurant r WHERE r.id = (SELECT u.activeRestaurantId FROM User u WHERE u.username = :username)")
    Optional<Restaurant> getCurrentRestaurantByUsername(@Param("username") String username);

    @Query("SELECT u.activeMenuId FROM User u WHERE u.username = :username")
    Optional<Long> getActiveMenuIdByUsername(@Param("username") String username);

    @Query("SELECT m.id FROM Menu m WHERE m.restaurant.id = :restaurantId ORDER BY m.id ASC LIMIT 1")
    Optional<Long> findFirstMenuIdByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("""
            SELECT DISTINCT m FROM Menu m
            LEFT JOIN FETCH m.categories c
            WHERE m.id = (SELECT u.activeMenuId FROM User u WHERE u.username = :username)
            """)
    Optional<Menu> getCurrentMenuByUsername(@Param("username") String username);

    void deleteByUsername(String username);

    @Query("SELECT MAX(u.organizationId) FROM User u")
    Optional<Long> findMaxOrganizationId();

    List<User> findAllByActiveRestaurantId(Long activeRestaurantId);

    @Query("SELECT u.enabled FROM User u WHERE u.username = :username")
    Optional<Integer> isUserEnabledByUsername(@Param("username") String username);

    @Query("SELECT u.active FROM User u WHERE u.username = :username")
    Optional<Boolean> isUserActiveByUsername(@Param("username") String username);

    Optional<User> findByEmailToken(String emailToken);
}
