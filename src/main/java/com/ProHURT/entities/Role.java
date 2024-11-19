package com.ProHURT.entities;

import com.ProHURT.auth.Permission;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_role")
public class Role implements GrantedAuthority, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NonNull
    private String name;

    // New: Added a Set of permissions to the Role class
    @ElementCollection(targetClass = Permission.class)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name));
        return authorities;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Getter
    public enum RoleType {
        ADMIN(Set.of(Permission.USER_MANAGE, Permission.ITEM_MANAGE, Permission.PURCHASE_ORDER_CREATE)),
        MANAGER(Set.of(Permission.INVENTORY_ADD, Permission.INVENTORY_MONITOR, Permission.PURCHASE_ORDER_GENERATE)),
        STAFF(Set.of(Permission.INVENTORY_VIEW, Permission.INVENTORY_REQUEST, Permission.INVENTORY_UPDATE));

        private final Set<Permission> permissions;

        RoleType(Set<Permission> permissions) {
            this.permissions = permissions;
        }

    }
}