package com.ProHURT.repositories;

import com.ProHURT.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface ingerits from JpaRepository, which provides metods
 * to CRUD operation. By extending JpaRepository, this interface
 * will have a methods like save(), findAll(), delate() and etc..,
 * automatically implemented at runtime by Spring Data JPA.
 */

public interface RoleRepository extends JpaRepository<Role, Long> {
}
