package pl.rarytas.hungry_scan_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.rarytas.hungry_scan_core.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CUSTOMER' OR r.name = 'ROLE_CUSTOMER_READONLY'")
    List<User> findAllCustomers();

}
