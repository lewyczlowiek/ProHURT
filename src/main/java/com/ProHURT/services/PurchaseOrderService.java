package com.ProHURT.services;

import com.ProHURT.entities.*;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.repositories.ItemRepository;
import com.ProHURT.repositories.PurchaseOrderLineItemRepository;
import com.ProHURT.repositories.PurchaseOrderRepository;
import com.ProHURT.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public String generateOrderNumber() {
        String maxOrderNumber = purchaseOrderRepository.findMaxOrderNumber();

        if (maxOrderNumber == null) {
            return "1";
        }

        long nextOrderNumber = Long.parseLong(maxOrderNumber) + 1;

        return String.valueOf(nextOrderNumber);
    }


    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {

        if (purchaseOrder.getOrderNumber() == null || purchaseOrder.getOrderNumber().isEmpty()) {
            purchaseOrder.setOrderNumber(generateOrderNumber());
        }

        if (purchaseOrderRepository.existsByOrderNumber(purchaseOrder.getOrderNumber())) {
            throw new IllegalArgumentException("Numer zamówienia już istnieje");
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }


    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder updatedOrder, List<Long> itemIds, List<Integer> quantities) throws ResourceNotFoundException {
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zamówienie o ID " + id + " nie zostało znalezione"));

        existingOrder.setStatus(updatedOrder.getStatus());

        if (updatedOrder.getStore() != null) {
            Store store = storeRepository.findById(updatedOrder.getStore().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sklep o ID " + updatedOrder.getStore().getId() + " nie został znaleziony"));
            existingOrder.setStore(store);
        }

        if (itemIds == null || quantities == null || itemIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Listy produktów i ilości muszą być zgodne rozmiarowo i nie mogą być null.");
        }

        List<PurchaseOrderLineItem> existingLineItems = purchaseOrderLineItemRepository.findByPurchaseOrderId(id);

        existingLineItems.removeIf(lineItem -> {
            Long itemId = lineItem.getItem().getId();
            if (itemIds.contains(itemId)) {
                int index = itemIds.indexOf(itemId);
                lineItem.setQuantity(quantities.get(index));
                purchaseOrderLineItemRepository.save(lineItem);
                itemIds.remove(index);
                quantities.remove(index);
                return false;
            } else {
                purchaseOrderLineItemRepository.delete(lineItem);
                return true;
            }
        });

        for (int i = 0; i < itemIds.size(); i++) {
            Long newItemId = itemIds.get(i);
            Integer quantity = quantities.get(i);

            Item newItem = itemRepository.findById(newItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produkt o ID " + newItemId + " nie został znaleziony"));

            PurchaseOrderLineItem newLineItem = new PurchaseOrderLineItem();
            newLineItem.setPurchaseOrder(existingOrder);
            newLineItem.setItem(newItem);
            newLineItem.setQuantity(quantity);

            purchaseOrderLineItemRepository.save(newLineItem);
        }
        return purchaseOrderRepository.save(existingOrder);
    }

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
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Zamówienie nie istnieje"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Produkt nie istnieje"));

        PurchaseOrderLineItem lineItem = new PurchaseOrderLineItem();
        lineItem.setQuantity(quantity);
        lineItem.setItem(item);
        lineItem.setPurchaseOrder(purchaseOrder);

        purchaseOrder.addPurchaseOrderLineItem(lineItem);

        System.out.println("Próba zapisu zamówienia: " + purchaseOrder.getOrderNumber());
        purchaseOrderRepository.save(purchaseOrder);

        System.out.println("Zamówienie zostało zapisane. Numer zamówienia: " + purchaseOrder.getOrderNumber());
        System.out.println("Pozycje w zamówieniu: " + purchaseOrder.getLineItems());
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

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

    public void deletePurchaseOrderLineItem(Long lineItemId) throws ResourceNotFoundException {
        if (purchaseOrderRepository.existsById(lineItemId)) {
            purchaseOrderRepository.deleteById(lineItemId);
        } else {
            throw new ResourceNotFoundException("Purchase Order line Item not found");
        }
    }
}
