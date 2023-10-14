package pl.rarytas.rarytas_restaurantside.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MenuItemServiceInterface {
    void save(MenuItem menuItem, MultipartFile file) throws IOException;

    List<MenuItem> findAll();

    Optional<MenuItem> findById(Integer id);
}
