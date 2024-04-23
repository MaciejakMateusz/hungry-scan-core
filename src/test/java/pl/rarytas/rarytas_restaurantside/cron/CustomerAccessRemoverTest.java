package pl.rarytas.rarytas_restaurantside.cron;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.JwtToken;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.repository.JwtTokenRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerAccessRemoverTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private CustomerAccessRemover customerAccessRemover;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveCustomersWithAccess() {
        List<User> customers = userService.findAllCustomers();
        List<JwtToken> tokens = jwtTokenRepository.findAll();
        assertEquals(3, customers.size());
        assertEquals(3, tokens.size());

        customerAccessRemover.controlJwtAndRemoveUsers();

        List<User> clearedUsers = userService.findAllCustomers();
        assertEquals(1, clearedUsers.size());
        assertEquals("0c9e683-8576", clearedUsers.get(0).getUsername());

        List<JwtToken> clearedTokens = jwtTokenRepository.findAll();
        assertEquals(1, clearedTokens.size());
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwYzllNjgzLTg1NzYiLCJpYXQiOjE3MTM4ODA5MjMsImV4cCI6MTcxMzk1MjkyM30.M28dOa0W5FApG8p2sgfUhLHylHO4hM5bAgOOgF2k5oU",
                clearedTokens.get(0).getToken());
    }
}