package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Profile;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.ProfileRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ProfileServiceImp implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ExceptionHelper exceptionHelper;
    private final UserRepository userRepository;

    public ProfileServiceImp(ProfileRepository profileRepository, ExceptionHelper exceptionHelper, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.exceptionHelper = exceptionHelper;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Profile profile) throws LocalizedException {
        com.hackybear.hungry_scan_core.entity.User currentUser = getCurrentUser();
        profile.setUsername(currentUser.getUsername());
        currentUser.addUserProfile(profile);
        userRepository.save(currentUser);
    }

    @Override
    public Set<Profile> findAllByUsername() throws LocalizedException {
        Object principal = getPrincipal();
        if (principal instanceof User user) {
            String username = user.getUsername();
            return profileRepository.findAllByUsername(username);
        } else {
            throw new LocalizedException("User details not found");
        }
    }

    @Override
    public Profile findById(Integer id) throws LocalizedException {
        return profileRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.profileService.profileNotFound", id));
    }

    @Override
    public Optional<String> getActiveProfileByUsername(String username) {
        return profileRepository.findActiveProfileByUsername(username);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        com.hackybear.hungry_scan_core.entity.User currentUser = getCurrentUser();
        Profile profile = findById(id);
        currentUser.removeUserProfile(profile);
        userRepository.save(currentUser);
        profileRepository.delete(profile);
    }

    @Override
    @Transactional
    public boolean authorize(String pin, Integer profileId) throws LocalizedException {
        Profile destinationProfile = findById(profileId);
        boolean isAuthorized = BCrypt.checkpw(pin, destinationProfile.getPin());
        if (!isAuthorized) {
            return false;
        }
        switchProfile(destinationProfile);
        return true;
    }

    private void switchProfile(Profile destinationProfile) {
        profileRepository.deactivateActiveProfile();
        destinationProfile.setActive(true);
        profileRepository.save(destinationProfile);
    }

    private com.hackybear.hungry_scan_core.entity.User getCurrentUser() throws LocalizedException {
        Object principal = getPrincipal();
        String username;
        if (principal instanceof User user) {
            username = user.getUsername();
        } else {
            throw new LocalizedException("User details not found");
        }
        return userRepository.findUserByUsername(username);
    }

    private Object getPrincipal() throws LocalizedException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new LocalizedException("Unauthorized request, please log in.");
        }
        return authentication.getPrincipal();
    }

}