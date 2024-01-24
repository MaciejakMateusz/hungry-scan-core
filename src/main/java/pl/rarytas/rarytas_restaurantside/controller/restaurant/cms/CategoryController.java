package pl.rarytas.rarytas_restaurantside.controller.restaurant.cms;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/restaurant/cms/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list() {
        return "restaurant/cms/categories/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("category", new Category());
        return "restaurant/cms/categories/add";
    }

    @PostMapping("/add")
    public String add(@Valid Category category, BindingResult br) {
        if (br.hasErrors()) {
            return "restaurant/cms/categories/add";
        }
        categoryService.save(category);
        return "redirect:/restaurant/cms/categories";
    }

    @PostMapping("/edit")
    public String updateItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("category", categoryService.findById(id).orElseThrow());
        return "restaurant/cms/categories/edit";
    }

    @Modifying
    @PostMapping("/update")
    public String updateItem(@Valid Category category, BindingResult br) {
        if (br.hasErrors()) {
            return "restaurant/cms/categories/edit";
        }
        categoryService.save(category);
        return "redirect:/restaurant/cms/categories";
    }

    @PostMapping("/delete")
    public String deleteItem(Model model,
                             @RequestParam Integer id) {
        model.addAttribute("category", categoryService.findById(id).orElseThrow());
        return "restaurant/cms/categories/delete";
    }

    @PostMapping("/remove")
    public String deleteItem(@ModelAttribute Category category) {
        categoryService.delete(category);
        return "redirect:/restaurant/cms/categories";
    }

    @ModelAttribute("categories")
    private List<Category> getCategories() {
        return categoryService.findAll();
    }
}
