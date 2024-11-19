package com.ProHURT.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StoreInventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", referencedColumnName = "id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId", referencedColumnName = "id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    public StoreInventory(Store store, Item item, int quantity, String status){
        this.store = store;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

    public void addItems(int count) {this.quantity += count;}

    public void removeItems(int count) {this.quantity -= count;}

    public void setStatus(String status) {this.status = status;}

    public boolean isBelowThreshold(int threshold) {return this.quantity < threshold;}
}
