package com.ProHURT.auth;

import com.ProHURT.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("authenticationRequest", new AuthenticationRequest());
        return "login";
    }

    @PostMapping
    public String login(@ModelAttribute AuthenticationRequest authenticationRequest, Model model) {
        try {
            authenticationService.authenticate(authenticationRequest);
            return "redirect:/index";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Błędne dane logowania!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

}
