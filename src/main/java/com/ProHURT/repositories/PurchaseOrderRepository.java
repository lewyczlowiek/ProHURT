package com.ProHURT.repositories;

import com.ProHURT.entities.OrderStatus;
import com.ProHURT.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * This repository extends JpaRepository to gain CRUD operations.
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByStatus(OrderStatus status);

    @Query("SELECT MAX(po.orderNumber) FROM PurchaseOrder po")
    String findMaxOrderNumber();

    boolean existsByOrderNumber(String orderNumber);

}
