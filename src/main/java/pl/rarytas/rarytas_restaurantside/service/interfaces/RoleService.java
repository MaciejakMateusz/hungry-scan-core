package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Role;

import java.util.Set;

public interface RoleService {

    Set<Role> findAll();

    Role findByName(String name);
}
