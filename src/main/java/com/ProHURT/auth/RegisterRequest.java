package com.ProHURT.auth;

import com.ProHURT.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private Role.RoleType role;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

}
