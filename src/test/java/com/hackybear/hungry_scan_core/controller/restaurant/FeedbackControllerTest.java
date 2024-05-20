package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.entity.Feedback;
import com.hackybear.hungry_scan_core.repository.FeedbackRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedbackControllerTest {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ApiRequestUtils apiRequestUtils;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
        log.info("H2 database initialized.");
        log.info("Creating feedbacks...");
        feedbackRepository.save(createFeedback(5, 4, 5, "Schabowy was fire"));
        feedbackRepository.save(createFeedback(4, 5, 4, "Pretty nice experience"));
        feedbackRepository.save(createFeedback(3, 3, 2, "Not hehe"));
        log.info("Feedbacks created.");
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void shouldFindAll() throws Exception {
        Map<String, Object> params = getPageableParams();
        Page<Feedback> allReviews = apiRequestUtils.fetchAsPage("/api/restaurant/feedback", params, Feedback.class);
        List<Feedback> reviews = allReviews.getContent();
        assertEquals(3, reviews.size());
        assertEquals("Schabowy was fire", reviews.get(0).getComment());
        assertEquals("Pretty nice experience", reviews.get(1).getComment());
        assertEquals("Not hehe", reviews.get(2).getComment());
    }

    private Map<String, Object> getPageableParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", 40);
        params.put("pageNumber", 0);
        return params;
    }

    private Feedback createFeedback(Integer food, Integer service, Integer vibe, String comment) {
        Feedback feedback = new Feedback();
        feedback.setFood(food);
        feedback.setService(service);
        feedback.setVibe(vibe);
        feedback.setComment(comment);
        return feedback;
    }

}