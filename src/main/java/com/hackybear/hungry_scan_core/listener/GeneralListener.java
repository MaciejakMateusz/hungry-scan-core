package com.hackybear.hungry_scan_core.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class GeneralListener {

    private static final String CREATED = "created";

    @PrePersist
    public void prePersist(final Object entity) {
        try {
            if (Objects.isNull(getCreatedDate(entity))) {
                setDateField(entity, "setCreated");
            } else {
                log.error("Creation time already exists, cannot overwrite.");
            }
        } catch (Exception e) {
            log.error("Error during prePersist", e);
        }
    }

    @PreUpdate
    public void preUpdate(final Object entity) {
        setDateField(entity, "setUpdated");
    }

    private void setDateField(Object entity, String methodName) {
        LocalDateTime now = LocalDateTime.now();
        try {
            Method method = entity.getClass().getMethod(methodName, LocalDateTime.class);
            method.invoke(entity, now);
        } catch (Exception e) {
            log.error("Error setting date field", e);
        }
    }

    private Object getCreatedDate(Object entity) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Field field = clazz.getDeclaredField(CREATED);
        field.setAccessible(true);
        return field.get(entity);
    }
}