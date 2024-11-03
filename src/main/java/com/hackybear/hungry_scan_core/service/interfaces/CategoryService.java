package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.CategoryCustomerDTO;
import com.hackybear.hungry_scan_core.dto.CategoryDTO;
import com.hackybear.hungry_scan_core.dto.CategoryFormDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.List;

public interface CategoryService {

    List<CategoryDTO> findAll() throws LocalizedException, AuthenticationException;

    List<Integer> findAllDisplayOrders() throws LocalizedException;

    List<CategoryDTO> updateDisplayOrders(List<CategoryFormDTO> categories) throws LocalizedException;

    Long countAll() throws LocalizedException;

    List<CategoryCustomerDTO> findAllAvailableAndVisible() throws LocalizedException;

    CategoryFormDTO findById(Long id) throws LocalizedException;

    void save(CategoryFormDTO category) throws Exception;

    void update(CategoryFormDTO categoryFormDTO) throws Exception;

    List<CategoryDTO> delete(Long id) throws LocalizedException, AuthenticationException;
}
