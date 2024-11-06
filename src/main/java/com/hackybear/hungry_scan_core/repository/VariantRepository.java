package com.hackybear.hungry_scan_core.repository;

import com.hackybear.hungry_scan_core.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findAllByMenuItemIdOrderByDisplayOrder(Long menuItemId);

    @Modifying
    @Query("UPDATE Variant v SET v.displayOrder = :displayOrder WHERE v.id = :variantId")
    void updateDisplayOrders(@Param("variantId") Long variantId, @Param("displayOrder") Integer displayOrder);

    @Query("SELECT MAX(v.displayOrder) FROM Variant v WHERE v.menuItemId = :menuItemId")
    Optional<Integer> findMaxDisplayOrderByMenuItemId(@Param("menuItemId") Long menuItemId);

}
