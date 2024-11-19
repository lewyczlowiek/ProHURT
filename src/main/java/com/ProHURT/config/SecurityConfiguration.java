package com.ProHURT.config;

import com.ProHURT.auth.CustomAccessDeniedHandler;
import com.ProHURT.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

                )
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers( "/auth/register", "/auth/login").permitAll()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/items/create/**").hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers("/storeInventory/deleteItem").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/storeInventory/edit/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/storeInventory/categories/create").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/orders/edit/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/orders/add").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/orders/delete").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("Nieudana próba logowania: " + authException.getMessage());
                            response.sendRedirect("/auth/login");
                        })
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .permitAll()
                        .defaultSuccessUrl("/index", true)
                        .failureUrl("/auth/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }
}
