package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Role;
import pl.rarytas.rarytas_restaurantside.repository.RoleRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RoleService;

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
