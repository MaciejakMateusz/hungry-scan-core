package pl.rarytas.rarytas_restaurantside.test_utils;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Page;

import java.io.Serial;

public class PageModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    public PageModule() {
        addDeserializer(Page.class, new PageDeserializer());
    }
}