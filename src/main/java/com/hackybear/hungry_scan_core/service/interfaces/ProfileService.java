package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Profile;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface ProfileService {
    void save(Profile profile) throws LocalizedException;

    Set<Profile> findAllByUsername() throws LocalizedException;

    Profile findById(Integer id) throws LocalizedException;

    Optional<String> getActiveProfileByUsername(String username);

    void delete(Integer id) throws LocalizedException;

    @Transactional
    boolean authorize(String pin, Integer profileId) throws LocalizedException;
}
