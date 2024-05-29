package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
}
