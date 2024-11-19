package com.ProHURT.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @NonNull
    private String location;

    @NonNull
    private String contactInformation;

    @NonNull
    private String storeType;

    @NonNull
    private Date openingDate;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL)
    private List<StoreInventory> storeInventories = new ArrayList<>();

    public Store(Long id) {this.id = id;}

}
