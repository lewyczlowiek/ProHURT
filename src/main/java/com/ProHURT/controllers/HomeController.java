//package com.ProHURT.controllers;
//
//
//import com.ProHURT.auth.AuthenticationService;
//import com.ProHURT.entities.Item;
//import com.ProHURT.entities.User;
//import com.ProHURT.services.ItemService;
//import com.ProHURT.services.UserService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.Comparator;
//import java.util.List;
//
//@Controller
//public class HomeController {
//    private final AuthenticationService authenticationService;
//    private final UserService userService;
//    private final ItemService itemService;
//
//    public HomeController(AuthenticationService authenticationService, UserService userService, ItemService itemService) {
//        this.authenticationService = authenticationService;
//        this.userService = userService;
//        this.itemService = itemService;
//    }
//
//    @GetMapping("/")
//    public String home() {
//        System.out.println("HomeController is accessed");
//        return "redirect:/auth/login";
//    }
//
//    @GetMapping("/index")
//    public String index(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
//            User user = userService.getUserByEmail(username);
//            model.addAttribute("firstname", user.getFirstname());
//        }
//
//
//        return "index";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/users")
//    public String listUsers(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
//            User user = userService.getUserByEmail(username);
//            model.addAttribute("username", user.getFirstname());
//        }
//        List<User> users = userService.getAllUsers();
//        model.addAttribute("users", users);
//        return "users";
//    }
//
//    @GetMapping("/items")
//    public String listItems(@RequestParam(value = "search", required = false) String search,
//                            @RequestParam(value = "category", required = false) String category,
//                            @RequestParam(value = "sort", required = false) String sort,
//                            Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
//            User user = userService.getUserByEmail(username);
//            model.addAttribute("firstname", user.getFirstname());
//        }
//        List<Item> items;
//        if (search != null && !search.isEmpty()) {
//            items = itemService.searchByName(search);
//        } else if (category != null && !category.isEmpty()) {
//            items = itemService.getItemsByCategory(category);
//        } else {
//            items = itemService.getAllItems();
//        }
//
//        if (sort != null) {
//            if ("asc".equals(sort)) {
//                items.sort(Comparator.comparingInt(Item::getQuantity));
//            } else if ("desc".equals(sort)) {
//                items.sort(Comparator.comparingInt(Item::getQuantity).reversed());
//            }
//        }
//
//        model.addAttribute("items", items);
//        model.addAttribute("search", search);
//        model.addAttribute("categories", itemService.getAllCategories());
//        return "items";
//    }
//}


package com.ProHURT.controllers;

import com.ProHURT.auth.AuthenticationService;
import com.ProHURT.entities.Item;
import com.ProHURT.entities.PurchaseOrder;
import com.ProHURT.entities.Store;
import com.ProHURT.entities.User;
import com.ProHURT.services.ItemService;
import com.ProHURT.services.PurchaseOrderService;
import com.ProHURT.services.StoreService;
import com.ProHURT.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ItemService itemService;
    private final StoreService storeService;
    private final PurchaseOrderService purchaseOrderService;

    public HomeController(AuthenticationService authenticationService, UserService userService, ItemService itemService, StoreService storeService, PurchaseOrderService purchaseOrderService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.itemService = itemService;
        this.storeService = storeService;
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping("/")
    public String home() {
        System.out.println("HomeController is accessed");
        return "redirect:/auth/login";
    }

    @GetMapping("/index")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("firstname", user.getFirstname());
        }
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);

        List<Item> limitedItems = items.stream().limit(11).collect(Collectors.toList());
        model.addAttribute("limitedItems", limitedItems);
        Store store = storeService.getStoreInfo();
        model.addAttribute("store", store);

        List<Store> allStores = storeService.getAllStores();
        model.addAttribute("allStores", allStores);

        List<Store> limitedStores = allStores.stream().limit(2).collect(Collectors.toList());
        model.addAttribute("limitedStores", limitedStores);

        List<PurchaseOrder> orders = purchaseOrderService.getAllPurchaseOrders();
        model.addAttribute("orders", orders);

        List<PurchaseOrder> limitedOrders = orders.stream().limit(5).collect(Collectors.toList());
        model.addAttribute("limitedOrders", limitedOrders);

        return "index";
    }

    @GetMapping("/stores")
    public String listStores(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("firstname", user.getFirstname());
        }

        List<Store> allStores = storeService.getAllStores();
        model.addAttribute("allStores", allStores);
        return "stores";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String listUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("username", user.getFirstname());
        }
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/items")
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

        if (sort != null) {
            if ("asc".equals(sort)) {
                items.sort(Comparator.comparingInt(Item::getQuantity));
            } else if ("desc".equals(sort)) {
                items.sort(Comparator.comparingInt(Item::getQuantity).reversed());
            }
        }

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("categories", itemService.getAllCategories());
        return "items";
    }
}
