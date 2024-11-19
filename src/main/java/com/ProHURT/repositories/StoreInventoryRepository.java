package com.ProHURT.repositories;

import com.ProHURT.entities.StoreInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreInventoryRepository extends JpaRepository<StoreInventory, Long> {
    List<StoreInventory > findByStoreId(Long store_id);
    List<StoreInventory> findByItemId(Long itemId);

    List<StoreInventory> findAllByItemId(Long itemId);
}
