package com.ProHURT.services;

import com.ProHURT.entities.*;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.repositories.ItemRepository;
import com.ProHURT.repositories.PurchaseOrderLineItemRepository;
import com.ProHURT.repositories.PurchaseOrderRepository;
import com.ProHURT.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    private final ItemRepository itemRepository;

    private final StoreRepository storeRepository;

   private final PurchaseOrderLineItemRepository purchaseOrderLineItemRepository;

   private final ItemService itemService;


    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, ItemRepository itemRepository, StoreRepository storeRepository, PurchaseOrderLineItemRepository purchaseOrderLineItemRepository, ItemService itemService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
        this.purchaseOrderLineItemRepository = purchaseOrderLineItemRepository;
        this.itemService = itemService;
    }

    // Metoda generująca numer zamówienia
    public String generateOrderNumber() {
        // Pobierz najwyższy numer zamówienia (zakładając, że numery są liczbami)
        String maxOrderNumber = purchaseOrderRepository.findMaxOrderNumber();

        if (maxOrderNumber == null) {
            // Jeśli to pierwsze zamówienie, zaczynamy od numeru 1
            return "1";
        }

        // Jeśli jest już zamówienie z najwyższym numerem, zwiększamy go o 1
        long nextOrderNumber = Long.parseLong(maxOrderNumber) + 1;

        return String.valueOf(nextOrderNumber);
    }

    // Create a new Purchase Order
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        // Jeśli numer zamówienia nie jest podany, generujemy nowy
        if (purchaseOrder.getOrderNumber() == null || purchaseOrder.getOrderNumber().isEmpty()) {
            purchaseOrder.setOrderNumber(generateOrderNumber());
        }

        if (purchaseOrderRepository.existsByOrderNumber(purchaseOrder.getOrderNumber())) {
            throw new IllegalArgumentException("Numer zamówienia już istnieje");
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }

    //Update existing Purchase Order
    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder updatedOrder, List<Long> itemIds, List<Integer> quantities) throws ResourceNotFoundException {

        // Pobranie zamówienia
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie o ID " + id + " nie zostało znalezione"));

        // Aktualizacja statusu i sklepu zamówienia
        existingOrder.setStatus(updatedOrder.getStatus());

        if (updatedOrder.getStore() != null) {
            Store store = storeRepository.findById(updatedOrder.getStore().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sklep o ID " + updatedOrder.getStore().getId() + " nie został znaleziony"));
            existingOrder.setStore(store);
        }

        // Aktualizacja pozycji zamówienia
        if (itemIds != null && quantities != null && itemIds.size() == quantities.size()) {

            // Mapowanie `itemId` do `quantity` dla wygody
            Map<Long, Integer> itemQuantityMap = new HashMap<>();
            for (int i = 0; i < itemIds.size(); i++) {
                itemQuantityMap.put(itemIds.get(i), quantities.get(i));
            }

            // Usuwanie produktów, które nie znajdują się w `itemIds`
            List<PurchaseOrderLineItem> existingLineItems = purchaseOrderLineItemRepository.findByPurchaseOrderId(id);
            for (PurchaseOrderLineItem lineItem : existingLineItems) {
                if (!itemQuantityMap.containsKey(lineItem.getItem().getId())) {
                    purchaseOrderLineItemRepository.delete(lineItem);
                }
            }

            // Aktualizacja lub dodawanie pozycji zamówienia
            for (Map.Entry<Long, Integer> entry : itemQuantityMap.entrySet()) {
                Long itemId = entry.getKey();
                int quantity = entry.getValue();

                Item item = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Produkt o ID " + itemId + " nie został znaleziony"));

                // Pobranie lub utworzenie nowej pozycji zamówienia
                PurchaseOrderLineItem lineItem = purchaseOrderLineItemRepository.findByPurchaseOrderAndItem(existingOrder, item)
                        .orElseGet(() -> {
                            PurchaseOrderLineItem newItem = new PurchaseOrderLineItem();
                            newItem.setPurchaseOrder(existingOrder);
                            newItem.setItem(item);
                            return newItem;
                        });

                // Ustawienie ilości
                lineItem.setQuantity(quantity);
                purchaseOrderLineItemRepository.save(lineItem);
            }
        }

        // Zapisanie głównego zamówienia po wprowadzeniu wszystkich zmian
        return purchaseOrderRepository.save(existingOrder);
    }

    public void updatePurchaseOrderWithItems(Long id, PurchaseOrder purchaseOrder, Map<Long, Integer> itemsToQuantities) {
        // Pobierz istniejące zamówienie
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie nie istnieje."));

        // Aktualizuj podstawowe dane
        existingOrder.setStatus(purchaseOrder.getStatus());
        existingOrder.setStore(purchaseOrder.getStore());

        // Aktualizuj produkty w zamówieniu
        List<PurchaseOrderLineItem> lineItems = existingOrder.getLineItems();

        // Mapuj istniejące elementy dla szybkiego dostępu
        Map<Long, PurchaseOrderLineItem> existingItemsMap = lineItems.stream()
                .collect(Collectors.toMap(item -> item.getItem().getId(), item -> item));

        for (Map.Entry<Long, Integer> entry : itemsToQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (existingItemsMap.containsKey(itemId)) {
                // Jeśli produkt już istnieje, zaktualizuj ilość
                existingItemsMap.get(itemId).setQuantity(quantity);
            } else {
                // Jeśli produkt jest nowy, dodaj go do zamówienia
                Item item = itemService.getItemById(itemId);
               //Item item = itemRepository.findById(itemId);
                PurchaseOrderLineItem newItem = new PurchaseOrderLineItem(item, quantity, existingOrder);
                lineItems.add(newItem);
            }
        }

        // Zapisz zmiany w bazie danych
        purchaseOrderRepository.save(existingOrder);
    }



    //Delete a Purchase order
    public void deletePurchaseOrder(Long id) throws ResourceNotFoundException {
        if (purchaseOrderRepository.existsById(id)) {
            purchaseOrderRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Purchase Order not found!");
        }
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {return purchaseOrderRepository.findById(id);}

    public List<PurchaseOrder> getAllPurchaseOrders() {return purchaseOrderRepository.findAll();}

    @Transactional
    public void addItemToPurchaseOrder(Long orderId, Long itemId, int quantity) {
        // Sprawdź, czy zamówienie o danym ID istnieje
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Zamówienie nie istnieje"));

        // Sprawdź, czy produkt o danym ID istnieje
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Produkt nie istnieje"));

        // Utwórz nową pozycję zamówienia
        PurchaseOrderLineItem lineItem = new PurchaseOrderLineItem();
        lineItem.setQuantity(quantity);
        lineItem.setItem(item);
        lineItem.setPurchaseOrder(purchaseOrder);

        // Dodaj nową pozycję do listy pozycji zamówienia
        purchaseOrder.addPurchaseOrderLineItem(lineItem);

        System.out.println("Próba zapisu zamówienia: " + purchaseOrder.getOrderNumber());
        purchaseOrderRepository.save(purchaseOrder);

        System.out.println("Zamówienie zostało zapisane. Numer zamówienia: " + purchaseOrder.getOrderNumber());
        System.out.println("Pozycje w zamówieniu: " + purchaseOrder.getLineItems());
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    //Get all Purchase order
    public PurchaseOrderLineItem getPurchaseOrderLineItemById(Long lineItemId) throws ResourceNotFoundException {
        return purchaseOrderLineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order Line Item not found"));
    }

    public List<PurchaseOrderLineItem> getPurchaseOrderLineItems(Long purchaseOrderId) throws ResourceNotFoundException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found"));
        var lineItems = purchaseOrderLineItemRepository.findByPurchaseOrderId(purchaseOrderId);
        return lineItems;
    }

    //Delete a purchase order line item by ItemId
    public void deletePurchaseOrderLineItem(Long lineItemId) throws ResourceNotFoundException {
        if (purchaseOrderRepository.existsById(lineItemId)) {
            purchaseOrderRepository.deleteById(lineItemId);
        } else {
            throw new ResourceNotFoundException("Purchase Order line Item not found");
        }
    }

    public void updateLineItemQuantity(Long orderId, Long itemId, int quantity) throws ResourceNotFoundException {
        // Wyszukiwanie zamówienia
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie o ID " + orderId + " nie zostało znalezione"));

        // Wyszukiwanie produktu
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Produkt o ID " + itemId + " nie został znaleziony"));

        // Wyszukiwanie pozycji zamówienia (line item)
        PurchaseOrderLineItem lineItem = purchaseOrderLineItemRepository.findByPurchaseOrderAndItem(order, item)
                .orElseThrow(() -> new ResourceNotFoundException("Pozycja zamówienia dla tego produktu nie istnieje"));

        // Sprawdzanie, czy ilość jest różna od obecnej
        if (lineItem.getQuantity() != quantity) {
            // Aktualizacja ilości
            lineItem.setQuantity(quantity);
            purchaseOrderLineItemRepository.save(lineItem);  // Zapisz zmiany
        } else {
            // Możesz dodać logikę logowania, jeśli ilość jest taka sama
            System.out.println("Ilość jest już taka sama, brak zmiany.");
        }
    }
}
