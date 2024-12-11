package com.ProHURT.controllers;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.ItemCategory;
import com.ProHURT.entities.User;
import com.ProHURT.services.CategoryService;
import com.ProHURT.services.ItemService;
import com.ProHURT.services.StoreService;
import com.ProHURT.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/storeInventory")
public class StoreInventoryController {

    private final UserService userService;
    private final ItemService itemService;
    private final StoreService storeService;
    private final CategoryService categoryService;

    public StoreInventoryController(UserService userService, ItemService itemService, StoreService storeService, CategoryService categoryService) {

        this.userService = userService;
        this.itemService = itemService;
        this.storeService = storeService;
        this.categoryService = categoryService;
    }


    @GetMapping
    public String listItems(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "sort", required = false) String sort,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("firstname", user.getFirstname());
        }
        List<Item> items;
        if (search != null && !search.isEmpty()) {
            items = itemService.searchByName(search);
        } else if (category != null && !category.isEmpty()) {
            items = itemService.getItemsByCategory(category);
        } else {
            items = itemService.getAllItems();
        }

        if ("asc".equals(sort)) {
            items.sort(Comparator.comparingInt(Item::getQuantity));
        } else if ("desc".equals(sort)) {
            items.sort(Comparator.comparingInt(Item::getQuantity).reversed());
        }

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("categories", itemService.getAllCategories());
        model.addAttribute("sort", sort);
        return "storeInventory";
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String editItem(@PathVariable Long id, Model model) {

        Item item = itemService.getItemById(id);
        model.addAttribute("item", item);

        List<ItemCategory> categories = itemService.getAllCategories();
        model.addAttribute("categories", categories);
        return "editItem";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String updateItem(@PathVariable Long id, @ModelAttribute Item item) {
        itemService.updateItem(id, item);
        return "redirect:/storeInventory";
    }


    @PostMapping("/updateQuantity/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        itemService.updateQuantity(id, quantity);
        return "redirect:/storeInventory";
    }


    @PostMapping("/deleteItem")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String deleteProduct(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        itemService.deleteItem(productId);
        redirectAttributes.addFlashAttribute("message", "Produkt został usunięty!");


        return "redirect:/storeInventory";
    }

    @GetMapping("/categories/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("itemCategory", new ItemCategory());
        return "addItemCategory";
    }

    @PostMapping("/categories/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String createCategory(@ModelAttribute ItemCategory itemCategory) {
        System.out.println("Dodawanie kategorii: " + itemCategory.getName());
        categoryService.createCategory(itemCategory);
        return "redirect:/storeInventory";
    }


}
