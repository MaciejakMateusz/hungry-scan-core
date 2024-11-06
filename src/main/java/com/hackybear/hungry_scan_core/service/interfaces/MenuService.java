package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Menu;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import javax.naming.AuthenticationException;
import java.util.List;

public interface MenuService {

    List<Menu> findAll() throws LocalizedException, AuthenticationException;

    Long countAll() throws LocalizedException;

    Menu findById(Long id) throws LocalizedException;

    void save(Menu menu) throws Exception;

    void delete(Long id) throws LocalizedException, AuthenticationException;
}
