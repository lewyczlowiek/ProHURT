package com.ProHURT.controllers;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.ItemCategory;
import com.ProHURT.services.CategoryService;
import com.ProHURT.services.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;


    @Autowired
    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String showCreateItemForm(Model model) {
        Item item = new Item();
        model.addAttribute("item", item);
        List<ItemCategory> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "item_form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String addItem(@Valid @ModelAttribute("item") Item item,
                          BindingResult result, Model model) {
        System.out.println("Item: " + item);

        if (result.hasErrors()) {
            System.out.println("Wystąpił błąd w przekazaniu formularza.");

            List<ItemCategory> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "item_form";
        }

        if (item.getCategory() == null || item.getCategory().getId() == null) {
            System.out.println("Kategoria jest pusta lub ID kategorii jest puste!");
            List<ItemCategory> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "item_form";
        }

        itemService.createItem(item);
        return "redirect:/items";
    }
}
