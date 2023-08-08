package pl.rarytas.rarytas_restaurantside.converter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.repository.OrderedItemRepository;

@Component
@Getter
@Slf4j
public class OrderedItemConverter implements Converter<String, OrderedItem> {

    private final OrderedItemRepository orderedItemRepository;

    public OrderedItemConverter(OrderedItemRepository orderedItemRepository) {
        this.orderedItemRepository = orderedItemRepository;
    }

    @Override
    public OrderedItem convert(@Nullable String source) {
        int id = -1;
        try {
            assert source != null;
            id = Integer.parseInt(source);
        } catch (NumberFormatException e) {
            log.error(String.valueOf(e));
        }
        return orderedItemRepository.findById(id).orElseThrow();
    }
}
