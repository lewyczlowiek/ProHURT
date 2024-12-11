package com.ProHURT.controllers;

import com.ProHURT.auth.AuthenticationService;
import com.ProHURT.auth.RegisterRequest;
import com.ProHURT.entities.User;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/createUser")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegisterRequest());
        return "create";
    }


    @PostMapping
    public String addUser(@ModelAttribute("registrationRequest") User registrationRequest, RedirectAttributes redirectAttributes) {
        User createdUser = userService.createUser(registrationRequest);
        redirectAttributes.addFlashAttribute("message", "Użytkownik dodany pomyślnie: " + createdUser.getFirstname() + " " + createdUser.getLastname());
        return "redirect:/users";
    }

    @GetMapping("/editUser")
    public String showEditUserForm(@RequestParam("userId") Long userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser")
    public String updateUser(@RequestParam("userId") Long userId, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("selfEditRoleError", true);
            return "redirect:/users/editUser?userId=" + userId;
        }

        userService.updateUser(userId, user);
        return "redirect:/users";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") Long userId,@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("selfDeleteError", true);
            return "redirect:/users";
        }

        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("message", "Użytkownik został usunięty!");
        return "redirect:/users";
    }
}
