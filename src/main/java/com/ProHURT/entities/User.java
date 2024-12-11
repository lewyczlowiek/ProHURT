package com.ProHURT.entities;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String firstname;
    private String lastname;

    @Column(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Role.RoleType role;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_managed_stores",
                joinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<Store> managedStores = new HashSet<>();

    public User(String firstname, String lastname, @NotNull String email, String password, Role.@NotNull RoleType role){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
        this.role = role;
    }

    public Long getId() {
        return Id;
    }

    public void addManagedStore(Store store) {this.managedStores.add(store);}

    public void removeManagedStore(Store store) {this.managedStores.remove(store);}

    public void setManagedStores(Set<Store> managedStores) {this.managedStores = managedStores;}


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert roles to a list of SimpleGrantedAuthorities
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
