package com.ProHURT.repositories;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.PurchaseOrder;
import com.ProHURT.entities.PurchaseOrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderLineItemRepository extends JpaRepository<PurchaseOrderLineItem, Long> {
    List<PurchaseOrderLineItem> findByPurchaseOrderId(Long purchaseOrderId);
    Optional<PurchaseOrderLineItem> findByPurchaseOrderAndItem(PurchaseOrder purchaseOrder, Item item);

}
