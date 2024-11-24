package com.ProHURT.controllers;

import com.ProHURT.entities.*;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.services.ItemService;
import com.ProHURT.services.PurchaseOrderService;
import com.ProHURT.services.StoreService;
import com.ProHURT.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final UserService userService;
    private final ItemService itemService;
    private final StoreService storeService;


    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, UserService userService, ItemService itemService, StoreService storeService) {
        this.purchaseOrderService = purchaseOrderService;
        this.userService = userService;
        this.itemService = itemService;
        this.storeService = storeService;
    }

    // Wyświetlenie wszystkich zamówień
    @GetMapping
    public String getAllPurchaseOrders(@RequestParam(required = false) String status, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("firstname", user.getFirstname());
        }

        List<PurchaseOrder> orders;
        if (status != null && !status.isEmpty()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = purchaseOrderService.getPurchaseOrdersByStatus(orderStatus);
        } else {
            orders = purchaseOrderService.getAllPurchaseOrders();
        }

        model.addAttribute("orders", orders);
        return "orders"; // Widok z listą zamówień
    }

    // Formularz do utworzenia nowego zamówienia
    @GetMapping("/add")
    public String createPurchaseOrderForm(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("order", new PurchaseOrder());
        return "addOrderForm"; // Formularz tworzenia nowego zamówienia
    }

    // Tworzenie nowego zamówienia
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String createPurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder,
                                      @RequestParam Long storeId,
                                      @RequestParam String status,
                                      @RequestParam List<Long> item_id,
                                      @RequestParam List<Integer> quantities,
                                      RedirectAttributes redirectAttributes) {

        System.out.println("Tworzenie zamówienia: " + purchaseOrder);
        System.out.println("Identyfikatory produktów: " + item_id);
        System.out.println("Ilości: " + quantities);

        Store store = storeService.getStoreById(storeId);
        purchaseOrder.setStore(store);
        purchaseOrder.setStatus(OrderStatus.valueOf(status)); // Ustawienie statusu na podstawie formularza

        // Utworzenie zamówienia
        PurchaseOrder createdOrder = purchaseOrderService.createPurchaseOrder(purchaseOrder);

        // Dodanie pozycji zamówienia
        for (int i = 0; i < item_id.size(); i++) {
            Long productId = item_id.get(i);
            int quantity = quantities.get(i);
            purchaseOrderService.addItemToPurchaseOrder(createdOrder.getId(), productId, quantity);
        }

        redirectAttributes.addFlashAttribute("success", "Zamówienie utworzone pomyślnie!");
        return "redirect:/orders";
    }

    // Formularz do edycji zamówienia
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String editPurchaseOrderForm(@PathVariable Long id, Model model) {
        Optional<PurchaseOrder> optionalOrder = purchaseOrderService.getPurchaseOrderById(id);
        if (optionalOrder.isEmpty()) {
            return "redirect:/orders"; // Jeśli zamówienie nie istnieje, wracamy do listy
        }

        PurchaseOrder purchaseOrder = optionalOrder.get();
        List<PurchaseOrderLineItem> lineItems = purchaseOrderService.getPurchaseOrderLineItems(id);
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("lineItems", lineItems);
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("statuses", OrderStatus.values());

        return "editOrder"; // Widok edytowania zamówienia
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String updatePurchaseOrder(@PathVariable Long id,
                                      @ModelAttribute PurchaseOrder purchaseOrder,
                                      @RequestParam String status,
                                      @RequestParam Long storeId,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        // Pobierz listy ręcznie, jeśli wymagane
        String[] itemIdsArray = request.getParameterValues("item_ids[]");
        String[] quantitiesArray = request.getParameterValues("quantities[]");

        // Tworzenie modyfikowalnych list z danych wejściowych
        List<Long> item_ids = itemIdsArray != null
                ? new ArrayList<>(Arrays.asList(itemIdsArray).stream().map(Long::parseLong).collect(Collectors.toList()))
                : null;
        List<Integer> quantities = quantitiesArray != null
                ? new ArrayList<>(Arrays.asList(quantitiesArray).stream().map(Integer::parseInt).collect(Collectors.toList()))
                : null;

        // Debugging
        System.out.println("Received item_ids: " + item_ids);
        System.out.println("Received quantities: " + quantities);

        try {
            // Update PurchaseOrder
            purchaseOrder.setStatus(OrderStatus.valueOf(status));
            purchaseOrder.setStore(storeService.getStoreById(storeId));
            purchaseOrderService.updatePurchaseOrder(id, purchaseOrder, item_ids, quantities);
            redirectAttributes.addFlashAttribute("success", "Zamówienie zaktualizowane pomyślnie!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/orders";
    }




    // Usuwanie zamówienia
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String deletePurchaseOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();


        try {
            purchaseOrderService.deletePurchaseOrder(id);
            redirectAttributes.addFlashAttribute("success", "Zamówienie usunięte pomyślnie!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }

    // Wyświetlenie szczegółów zamówienia i pozycji
    @GetMapping("/{id}")
    public String getPurchaseOrderDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PurchaseOrder> optionalOrder = purchaseOrderService.getPurchaseOrderById(id);
        if (optionalOrder.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono zamówienia o ID: " + id);
            return "redirect:/orders";
        }
        PurchaseOrder purchaseOrder = optionalOrder.get();
        List<PurchaseOrderLineItem> lineItems;  // Zmiana z Set na List
        try {
            lineItems = purchaseOrderService.getPurchaseOrderLineItems(id);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders";
        }

        double totalOrderPrice = lineItems.stream()
                .mapToDouble(lineItem -> lineItem.getItem().getPrice() * lineItem.getQuantity())
                .sum();


        model.addAttribute("purchaseOrder", purchaseOrder);
        model.addAttribute("lineItems", lineItems);
        model.addAttribute("unitPrices", lineItems.stream().map(lineItem -> lineItem.getItem().getPrice()).toList());
        model.addAttribute("totalPrices", lineItems.stream().map(lineItem -> lineItem.getItem().getPrice() * lineItem.getQuantity()).toList());
        model.addAttribute("totalOrderPrice", totalOrderPrice);
        return "/order-details";
    }



    // Formularz do dodania pozycji zamówienia
    @GetMapping("/{purchaseOrderId}/items/new")
    public String createPurchaseOrderLineItemForm(@PathVariable Long purchaseOrderId, Model model, RedirectAttributes redirectAttributes) {
        Optional<PurchaseOrder> optionalOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
        if (optionalOrder.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono zamówienia o ID: " + purchaseOrderId);
            return "redirect:/orders";
        }
        model.addAttribute("purchaseOrderId", purchaseOrderId);
        return "orders/itemForm"; // Formularz dodania pozycji
    }

    // Dodanie pozycji zamówienia
    @PostMapping("/{purchaseOrderId}/items")
    public String addItemToPurchaseOrder(@PathVariable Long purchaseOrderId, @RequestParam Long itemId, @RequestParam int quantity, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.addItemToPurchaseOrder(purchaseOrderId, itemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Produkt dodany do zamówienia!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + purchaseOrderId;
    }

    // Usunięcie pozycji zamówienia
    @PostMapping("/{purchaseOrderId}/items/{lineItemId}/delete")
    public String deletePurchaseOrderLineItem(@PathVariable Long purchaseOrderId, @PathVariable Long lineItemId, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.deletePurchaseOrderLineItem(lineItemId);
            redirectAttributes.addFlashAttribute("success", "Pozycja zamówienia usunięta pomyślnie!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + purchaseOrderId;
    }
}
