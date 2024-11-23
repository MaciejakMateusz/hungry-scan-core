package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static com.hackybear.hungry_scan_core.utility.Fields.USER_MENU_ID;
import static com.hackybear.hungry_scan_core.utility.Fields.USER_RESTAURANT_ID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u FROM User u
            WHERE u.organizationId = :organizationId
            AND u.id != :currentUserId
            AND u.forename != 'temp'
            """)
    TreeSet<User> findAllByOrganizationId(@Param("organizationId") Long organizationId, @Param("currentUserId") Long currentUserId);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CUSTOMER' OR r.name = 'ROLE_CUSTOMER_READONLY'")
    List<User> findAllCustomers();

    Optional<User> findByUsername(String username);

    @Cacheable(value = USER_MENU_ID, key = "#username")
    @Query("SELECT u.activeMenuId FROM User u WHERE u.username = :username")
    Long getActiveMenuIdByUsername(@Param("username") String username);

    @Cacheable(value = USER_RESTAURANT_ID, key = "#username")
    @Query("SELECT u.activeRestaurantId FROM User u WHERE u.username = :username")
    Long getActiveRestaurantIdByUsername(@Param("username") String username);

    void deleteByUsername(String username);

    @Query("SELECT MAX(u.organizationId) FROM User u")
    Optional<Long> findMaxOrganizationId();

    List<User> findAllByActiveRestaurantId(Long activeRestaurantId);

    @Query("SELECT u.enabled FROM User u WHERE u.username = :username")
    Optional<Integer> isUserEnabledByUsername(@Param("username") String username);

    Optional<User> findByEmailToken(String emailToken);
}
