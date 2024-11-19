package com.ProHURT.services;

import com.ProHURT.entities.Store;
//import com.ProHURT.entities.StoreInventory;
import com.ProHURT.entities.StoreInventory;
import com.ProHURT.exceptions.ResourceNotFoundException;
//import com.ProHURT.repositories.StoreInventoryRepository;
import com.ProHURT.repositories.StoreInventoryRepository;
import com.ProHURT.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public StoreService(StoreRepository storeRepository,StoreInventoryRepository storeInventoryRepository) {
        this.storeRepository = storeRepository;
        this.storeInventoryRepository = storeInventoryRepository;
    }

    public Store getStoreInfo() {
        // Zakładamy, że w tabeli jest jeden sklep, możesz zmienić tę metodę jeśli masz wiele sklepów.
        return storeRepository.findById(1L).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }


    public Store getStoreById(Long id) throws   ResourceNotFoundException {
        Store store = storeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Store not found"));
        List<StoreInventory> inventory = storeInventoryRepository.findByStoreId(store.getId());

        Hibernate.initialize(inventory);

        store.setStoreInventories(inventory);
        return store;
    }

}
