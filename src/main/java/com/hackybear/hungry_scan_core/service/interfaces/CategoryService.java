package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.List;

public interface CategoryService {

    List<CategoryDTO> findAll(Long activeMenuId) throws LocalizedException, AuthenticationException;

    List<Integer> findAllDisplayOrders(Long activeMenuId) throws LocalizedException;

    void updateDisplayOrders(List<CategoryFormDTO> categories, Long activeMenuId) throws LocalizedException;

    Long countAll(Long activeMenuId) throws LocalizedException;

    List<CategoryCustomerDTO> findAllAvailableAndVisible(Long activeMenuId) throws LocalizedException;

    CategoryFormDTO findById(Long id) throws LocalizedException;

    void save(CategoryFormDTO category, Long activeMenuId) throws Exception;

    void update(CategoryFormDTO categoryFormDTO, Long activeMenuId) throws Exception;

    void delete(Long id, Long activeMenuId) throws LocalizedException, AuthenticationException;
}
