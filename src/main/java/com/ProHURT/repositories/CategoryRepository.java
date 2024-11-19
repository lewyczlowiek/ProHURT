package com.ProHURT.repositories;

import com.ProHURT.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<ItemCategory, Long> {
    //Basic CRUD methods are already provided by JPA.
}
