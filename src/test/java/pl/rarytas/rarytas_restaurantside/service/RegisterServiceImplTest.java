package pl.rarytas.rarytas_restaurantside.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RegisterService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterServiceImplTest {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void shouldSaveUser() {
        User user = createCorrectUser();
        registerService.saveUser(user);
        User savedUser = userService.findByUsername("exampleUser");
        assertEquals("example@example.com", savedUser.getEmail());

        Set<Role> roleSet = savedUser.getRoles();
        assertTrue(hasRoleWithName(roleSet, "ROLE_USER"));
    }

    @Test
    @Transactional
    void shouldSaveAdmin() {
        User admin = createCorrectAdmin();
        registerService.saveAdmin(admin);
        User savedAdmin = userService.findByUsername("exampleAdmin");
        assertEquals("admin@admino.com", savedAdmin.getEmail());

        Set<Role> roleSet = savedAdmin.getRoles();

        assertTrue(hasRoleWithName(roleSet, "ROLE_USER"));
        assertTrue(hasRoleWithName(roleSet, "ROLE_ADMIN"));
    }

    private User createCorrectUser() {
        User user = new User();
        user.setEmail("example2@example.com");
        user.setUsername("exampleUser2");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    private User createCorrectAdmin() {
        User user = new User();
        user.setEmail("admin2@admino.com");
        user.setUsername("exampleAdmin2");
        user.setPassword("Example123!");
        user.setRepeatedPassword("Example123!");
        return user;
    }

    private boolean hasRoleWithName(Set<Role> roles, String roleName) {
        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}