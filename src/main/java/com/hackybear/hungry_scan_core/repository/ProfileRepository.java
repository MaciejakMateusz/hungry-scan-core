package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Set<Profile> findAllByUsername(String username);

    @Query("SELECT p.name FROM Profile p WHERE p.username = :username AND p.isActive = true")
    Optional<String> findActiveProfileByUsername(@Param("username") String username);

    @Modifying
    @Query("UPDATE Profile p SET p.isActive = false WHERE p.isActive = true")
    void deactivateActiveProfile();
}
