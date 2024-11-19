package com.ProHURT.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numer zamówienia jest wymagany")
    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderLineItem> lineItems = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId", referencedColumnName = "id", nullable = false)
    private Store store;

    // Konstruktor z parametrami
    public PurchaseOrder(Long id, @NonNull String orderNumber, @NonNull OrderStatus status, List<PurchaseOrderLineItem> lineItems, Store store) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.lineItems = lineItems;  // Prostsza wersja, bez null check
        this.store = store;
    }

    // Dodajemy metodę pomocniczą do dodawania elementów do listy
    public void addPurchaseOrderLineItem(PurchaseOrderLineItem purchaseOrderLineItem) {
        lineItems.add(purchaseOrderLineItem);
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", store=" + store +
                ", status=" + status.getDisplayName() +  // Wyświetlanie statusu
                '}';
    }
}