package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.Label;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.util.List;

public interface LabelService {
    void save(Label label);

    List<Label> findAll();

    Label findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}
