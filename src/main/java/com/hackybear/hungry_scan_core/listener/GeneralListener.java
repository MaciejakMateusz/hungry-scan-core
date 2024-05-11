package com.hackybear.hungry_scan_core.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class GeneralListener {

    @PrePersist
    public void prePersist(final Object entity) {
        try {
            if (Objects.isNull(getFieldValue(entity, "created"))) {
                setFieldDate(entity, "setCreated");
                log.info("Creation time set to: {}", LocalDateTime.now());
            } else {
                log.error("Creation time already exists, cannot overwrite.");
            }
        } catch (Exception e) {
            log.error("Error during prePersist", e);
        }
    }

    @PreUpdate
    public void preUpdate(final Object entity) {
        setFieldDate(entity, "setUpdated");
        log.info("Update time set to: {}", LocalDateTime.now());
    }

    @PreRemove
    public void preRemove(Object entity) {
        try {
            Object idValue = getFieldValue(entity, "id");
            log.info("Entity {} removed with ID: {}", entity.getClass().getSimpleName(), idValue);
        } catch (Exception e) {
            log.error("Error during preRemove", e);
        }
    }

    private void setFieldDate(Object entity, String methodName) {
        LocalDateTime now = LocalDateTime.now();
        try {
            Method method = entity.getClass().getMethod(methodName, LocalDateTime.class);
            method.invoke(entity, now);
        } catch (Exception e) {
            log.error("Error setting 'created' date field", e);
        }
    }

    private Object getFieldValue(Object entity, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(entity);
    }
}