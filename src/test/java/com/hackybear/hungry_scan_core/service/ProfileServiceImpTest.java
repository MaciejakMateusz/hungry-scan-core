package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Profile;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ProfileService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileServiceImpTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    @Sql("/data-h2.sql")
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @WithMockUser(username = "admin")
    @WithUserDetails(value = "admin")
    public void shouldFindAllByUsername() throws LocalizedException {
        Set<Profile> profiles = profileService.findAllByUsername();
        assertEquals(3, profiles.size());
        assertTrue(profiles.stream().anyMatch(profile -> "Profile 3".equals(profile.getName())));
    }

    @Test
    public void shouldFindById() throws LocalizedException {
        Profile profile = profileService.findById(2);
        assertEquals("Profile 2", profile.getName());
    }

    @Test
    public void shouldNotFindById() {
        LocalizedException localizedException = assertThrows(LocalizedException.class, () -> profileService.findById(4));
        assertEquals("Profil z podanym ID = 4 nie istnieje.", localizedException.getLocalizedMessage());
    }

    @Test
    @WithMockUser(username = "mati")
    @Transactional
    @Rollback
    public void shouldInsertNew() throws Exception {
        Profile profile = createProfile("Test profile");

        profileService.save(profile);

        Set<Profile> profiles = profileService.findAllByUsername();
        assertTrue(profiles.stream().anyMatch(p -> "Test profile".equals(p.getName())));
        User user = userRepository.findUserByUsername("mati");
        assertEquals(1, user.getProfiles().size());
    }

    @Test
    @WithMockUser(username = "mati")
    @Transactional
    @Rollback
    public void shouldNotInsertWithIncorrectName() {
        Profile profile = createProfile("");
        assertThrows(ConstraintViolationException.class, () -> profileService.save(profile));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    @Rollback
    public void shouldNotInsertWithNonUniqueName() {
        Profile profile = createProfile("Profile 3"); // Profile name already exists - inserted in data-h2.sql
        assertThrows(DataIntegrityViolationException.class, () -> profileService.save(profile));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    @Rollback
    public void shouldUpdate() throws Exception {
        Profile existingProfile = profileService.findById(1);
        assertEquals("Profile 1", existingProfile.getName());

        existingProfile.setName("Profile one");
        profileService.save(existingProfile);

        Profile updatedProfile = profileService.findById(1);
        assertEquals("Profile one", updatedProfile.getName());
    }

    @Test
    @Transactional
    @Rollback
    public void shouldDelete() throws LocalizedException {
        Profile profile = profileService.findById(3);
        assertEquals("Profile 3", profile.getName());

        profileService.delete(3);

        assertThrows(LocalizedException.class, () -> profileService.findById(3));
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    @Rollback
    public void shouldAuthorizeAndSwitch() throws LocalizedException {
        Profile activeProfile = getActiveByUsername("admin");
        assert Objects.nonNull(activeProfile);
        assertTrue(activeProfile.isActive());

        boolean isAuthorized = profileService.authorize("1234", 2);

        assertTrue(isAuthorized);
        activeProfile = getActiveByUsername("admin");
        assertEquals("Profile 2", activeProfile.getName());
    }

    @Test
    @WithMockUser(username = "admin")
    @Transactional
    @Rollback
    public void shouldNotAuthorizeAndSwitch() throws LocalizedException {
        Profile activeProfile = getActiveByUsername("admin");
        assert Objects.nonNull(activeProfile);
        assertTrue(activeProfile.isActive());

        //Wrong password case
        boolean isAuthorized = profileService.authorize("4321", 2);

        assertFalse(isAuthorized);
        activeProfile = getActiveByUsername("admin");
        assertEquals("Profile 1", activeProfile.getName());

        //Profile not found case
        assertThrows(LocalizedException.class, () -> profileService.authorize("1234", 88));
    }

    private Profile createProfile(String name) {
        Profile profile = new Profile();
        profile.setName(name);
        profile.setImageName("Test image.png");
        profile.setPin("1234");
        return profile;
    }

    private Profile getActiveByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        return user.getProfiles()
                .stream()
                .filter(Profile::isActive)
                .findFirst()
                .orElse(null);
    }

}