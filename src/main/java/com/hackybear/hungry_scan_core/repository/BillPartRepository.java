package com.hackybear.hungry_scan_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hackybear.hungry_scan_core.entity.BillPart;

@Repository
public interface BillPartRepository extends JpaRepository<BillPart, Long> {
}
