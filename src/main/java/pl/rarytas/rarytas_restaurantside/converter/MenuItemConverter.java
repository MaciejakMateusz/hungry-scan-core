package pl.rarytas.rarytas_restaurantside.converter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;

@Component
@Getter
@Slf4j
public class MenuItemConverter implements Converter<String, MenuItem> {

    private final MenuItemRepository menuItemRepository;

    public MenuItemConverter(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public MenuItem convert(@Nullable String source) {
        int id = -1;
        try {
            assert source != null;
            id = Integer.parseInt(source);
        } catch (NumberFormatException e) {
            log.error(String.valueOf(e));
        }
        return menuItemRepository.findById(id).orElseThrow();
    }
}