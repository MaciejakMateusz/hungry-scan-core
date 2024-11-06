package com.hackybear.hungry_scan_core.service.interfaces;

import java.util.List;

public interface PersistenceService<T> {

    List<T> findAll() throws Exception;

    T findById(Long id) throws Exception;

    void save(T t);

    void update(T t);

    void delete(Long id);
}
