package pl.rarytas.rarytas_restaurantside.controller.cms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.repository.CategoryRepository;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemRepository;
import pl.rarytas.rarytas_restaurantside.service.MenuItemService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/restaurant/cms/items")
public class MenuItemController {
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository, MenuItemService menuItemService) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemService = menuItemService;
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
    public String addItem(@ModelAttribute MenuItem menuItem,
                          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        menuItemService.save(menuItem, imageFile);
        return "redirect:/restaurant/cms/items";
    }

    @PostMapping("/edit")
    public String updateItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("menuItem", menuItemRepository.findById(id).orElseThrow());
        return "restaurant/cms/items/edit";
    }

    @PostMapping("/update")
    public String updateItem(@ModelAttribute MenuItem menuItem,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        menuItemService.save(menuItem, imageFile);
        return "redirect:/restaurant/cms/items";
    }

    @PostMapping("/delete")
    public String deleteItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("menuItem", menuItemRepository.findById(id).orElseThrow());
        return "restaurant/cms/items/delete";
    }

    @PostMapping("/remove")
    public String deleteItem(@ModelAttribute MenuItem menuItem) {
        menuItemRepository.delete(menuItem);
        return "redirect:/restaurant/cms/items";
    }

    @ModelAttribute("menuItems")
    private List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    @ModelAttribute("categories")
    private List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
