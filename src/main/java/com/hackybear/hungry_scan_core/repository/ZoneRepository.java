package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findAllByOrderByDisplayOrder();

}