package com.ProHURT.services;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.Store;
import com.ProHURT.entities.StoreInventory;
import com.ProHURT.repositories.ItemRepository;
import com.ProHURT.repositories.StoreInventoryRepository;
import com.ProHURT.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StoreInventoryService {
       private final StoreInventoryRepository storeInventoryRepository;
       private final StoreRepository storeRepository;
       private final ItemRepository itemRepository;

       public StoreInventoryService(StoreInventoryRepository storeInventoryRepository, StoreRepository storeRepository, ItemRepository itemRepository) {
           this.storeInventoryRepository = storeInventoryRepository;
           this.storeRepository = storeRepository;
           this.itemRepository =  itemRepository;
       }

       public List<StoreInventory> getAllInventories() {
           return storeInventoryRepository.findAll();
       }

    @Transactional
    public StoreInventory addItemToStoreInventory(Long storeId, Long itemId, int quantity, String status) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not found!"));

        StoreInventory storeInventory = new StoreInventory(store, item, quantity, status);
        return storeInventoryRepository.save(storeInventory);
    }

    @Transactional
    public StoreInventory updateStoreInventory(Long inventoryId, int quantity, String status) {
        StoreInventory inventory = storeInventoryRepository.findById(inventoryId).orElseThrow(() -> new IllegalArgumentException("Inventory not found!"));
        inventory.setQuantity(quantity);
        inventory.setStatus(status);
        return storeInventoryRepository.save(inventory);
    }

    public void deleteStoreInventory(Long inventoryId) {
        storeInventoryRepository.deleteById(inventoryId);
    }

    @Transactional
    public StoreInventory updateInventoryQuantity(Long inventoryId, int quantityChange) {
        StoreInventory inventory = storeInventoryRepository.findById(inventoryId).orElseThrow(() -> new IllegalArgumentException("Inventory not found!"));
        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        return storeInventoryRepository.save(inventory);
    }
}
