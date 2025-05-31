package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.repository.RoleRepository;
import com.hackybear.hungry_scan_core.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Set<Role> findAll() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
