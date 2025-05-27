package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.PricePlanType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricePlanTypeRepository extends JpaRepository<PricePlanType, String> {
}
