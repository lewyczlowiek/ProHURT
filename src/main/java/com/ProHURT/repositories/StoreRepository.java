package com.ProHURT.repositories;

import com.ProHURT.entities.Store;
//import com.ProHURT.entities.StoreInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByStoreTypeContainingIgnoreCase(String storeType);
    List<Store> findByLocationContainingIgnoreCase(String location);
    List<Store> findByNameContainingIgnoreCase(String name);
    List<Store> findByOpeningDateAfter(Date openingDate);
}
