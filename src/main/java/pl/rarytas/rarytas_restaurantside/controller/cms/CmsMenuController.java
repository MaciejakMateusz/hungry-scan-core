package pl.rarytas.rarytas_restaurantside.controller.cms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurant/cms")
@Slf4j
public class CmsMenuController {
    @GetMapping
    public String menu() {
        return "restaurant/cms/menu";
    }
}
