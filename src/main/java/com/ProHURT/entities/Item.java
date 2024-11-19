package com.ProHURT.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item implements Serializable {

    // Getter method
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "id", nullable = false)
    private ItemCategory category;

    @Positive
    private Double price;

    @Positive
    private Integer initialQuantity;


    @Positive
    private Integer quantity;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private Set<PurchaseOrderLineItem> purchaseOrderLineItem = new HashSet<>();


    public Item(Long id) {
        this.id = id;
    }

    public Item(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(Long id, String name, String description, Double price, Integer initialQuantity, Integer quantity, ItemCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.initialQuantity = initialQuantity;
        this.quantity = quantity;
        this.category = category;
    }


    /**
     * Sets the id for the item.
     *
     * @param id the new id for the item.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the name for the item.
     *
     * @param name the new name for the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description for the item.
     *
     * @param description the new description for the item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the category for the item.
     *
     * @param category the new category for the item.
     */
    public void setCategory(ItemCategory category) {
        this.category = category;
    }


    /**
     * Sets the current quantity for the item.
     *
     * @param quantity the new current quantity for the item.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the price for the item.
     *
     * @param price the new price for the item.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Sets the initial quantity for the item.
     *
     * @param initialQuantity the new initial quantity for the item.
     */
    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", initialQuantity=" + initialQuantity +
                ", quantity=" + quantity +
                '}';
    }
}
