package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.Role;
import pl.rarytas.hungry_scan_core.repository.RoleRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.RoleService;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImp implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<Role> findAll() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
