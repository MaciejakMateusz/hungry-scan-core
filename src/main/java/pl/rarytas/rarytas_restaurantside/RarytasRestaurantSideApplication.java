package pl.rarytas.rarytas_restaurantside;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import pl.rarytas.rarytas_restaurantside.filter.AuthFilter;

@SpringBootApplication
public class RarytasRestaurantSideApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RarytasRestaurantSideApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RarytasRestaurantSideApplication.class, args);
    }

}
