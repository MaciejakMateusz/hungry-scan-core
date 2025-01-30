package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.QrScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrScanRepository extends JpaRepository<QrScan, String> {

    Optional<QrScan> findByFootprint(String footprint);

    List<QrScan> findAllByRestaurantToken(String restaurantToken);

}
