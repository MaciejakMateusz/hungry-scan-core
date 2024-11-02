package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findAllByMenuItemIdOrderByDisplayOrder(Long menuItemId);

}
