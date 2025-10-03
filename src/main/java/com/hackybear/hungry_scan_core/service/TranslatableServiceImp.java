package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.TranslatableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.TranslatableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.hackybear.hungry_scan_core.utility.Fields.CATEGORIES_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.MENUS_ALL;

@Service
@RequiredArgsConstructor
public class TranslatableServiceImp implements TranslatableService {

    @Value("${deepl.api.key}")
    private String AUTH_KEY;

    private final TranslatableRepository translatableRepository;
    private final RestTemplate restTemplate;

    @Override
    @Caching(evict = {
            @CacheEvict(value = CATEGORIES_ALL, key = "#activeMenuId"),
            @CacheEvict(value = MENUS_ALL, key = "#activeRestaurantId")
    })
    public void saveAll(List<Translatable> translatables, Long activeMenuId, Long activeRestaurantId) {
        translatableRepository.saveAll(translatables);
    }

    @Override
    public String translate(Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DeepL-Auth-Key " + AUTH_KEY);
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api-free.deepl.com/v2/translate",
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

}
