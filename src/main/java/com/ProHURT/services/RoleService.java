package com.ProHURT.services;

import com.ProHURT.entities.Role;
import com.ProHURT.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {this.roleRepository = roleRepository;}

    //Create new role
    public Role createRole(Role role) {return roleRepository.save(role);}

    //Update an existing role
    public Role updateRole(Long id, Role role) throws Exception {
        if (roleRepository.existsById(id)) {
            role.setId(id);
            return roleRepository.save(role);
        } else {
            throw new Exception("Role not found!");
        }
    }

    //Delete existing role by Id
    public void deleteRole(Long id) throws Exception {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new Exception("Role not found!");
        };
    }

    //Get all roles by Id
    public List<Role> getAllRoles() {return roleRepository.findAll();}

    //Get specific role by ID
    public Optional<Role> getRoleById(Long id) {return roleRepository.findById(id);}

}
