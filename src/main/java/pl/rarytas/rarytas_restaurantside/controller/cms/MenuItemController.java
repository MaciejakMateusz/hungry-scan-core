package pl.rarytas.rarytas_restaurantside.controller.cms;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/restaurant/cms/items")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;

    public MenuItemController(MenuItemService menuItemService, CategoryService categoryService) {
        this.menuItemService = menuItemService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String itemsList() {
        return "restaurant/cms/items/list";
    }

    @GetMapping("/add")
    public String addItem(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        return "restaurant/cms/items/add";
    }

    @PostMapping("/add")
    public String addItem(@Valid MenuItem menuItem,
                          BindingResult br,
                          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (br.hasErrors()) {
            return "restaurant/cms/items/add";
        }
        menuItemService.save(menuItem, imageFile);
        return "redirect:/restaurant/cms/items";
    }

    @PostMapping("/edit")
    public String updateItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("menuItem", menuItemService.findById(id).orElseThrow());
        return "restaurant/cms/items/edit";
    }

    @PostMapping("/update")
    public String updateItem(@Valid MenuItem menuItem,
                             BindingResult br,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (br.hasErrors()) {
            return "restaurant/cms/items/edit";
        }
        menuItemService.save(menuItem, imageFile);
        return "redirect:/restaurant/cms/items";
    }

    @PostMapping("/delete")
    public String deleteItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("menuItem", menuItemService.findById(id).orElseThrow());
        return "restaurant/cms/items/delete";
    }

    @PostMapping("/remove")
    public String deleteItem(@ModelAttribute MenuItem menuItem) {
        menuItemService.delete(menuItem);
        return "redirect:/restaurant/cms/items";
    }

    @ModelAttribute("menuItems")
    private List<MenuItem> getAllItems() {
        return menuItemService.findAll();
    }

    @ModelAttribute("categories")
    private List<Category> getAllCategories() {
        return categoryService.findAll();
    }
}
