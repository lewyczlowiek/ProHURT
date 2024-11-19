package com.ProHURT.services;

import com.ProHURT.entities.Store;
import com.ProHURT.entities.User;
import com.ProHURT.exceptions.GlobalExceptionHandler;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.repositories.StoreRepository;
import com.ProHURT.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final SessionFactory sessionFactory;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, StoreRepository storeRepository, SessionFactory sessionFactory, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user, HttpServletRequest request, HttpServletResponse response) throws ResourceNotFoundException {
        return userRepository.findById(id).map(existingUser -> {
            // Sprawdź, czy rola zmienia się z ADMIN na inną
            if (existingUser.getRole().equals("ADMIN") && !user.getRole().equals("ADMIN")) {
                // Wyloguj użytkownika
                SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

                // Dodaj tutaj logikę, aby ustawić komunikat o konieczności ponownego logowania
            }

            // Zaktualizuj dane użytkownika
            user.setId(id);
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User with this id wasn't found!"));
    }

    public User updateUser(Long id, User user) throws ResourceNotFoundException {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setFirstname(user.getFirstname());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User with this id wasn't found!"));
    }

    public void deleteUser(Long id) throws ResourceNotFoundException {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("User not found!");
        }
    }


    //Get user by id
    @Transactional
    public User getUserById(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with this id not found!"));
        Hibernate.initialize(user.getManagedStores());
        return  user;
    }

    public User findById(Long id) {return userRepository.findById(id).orElse(null);}

    @Transactional
    //Get User by mail
    public User getUserByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User with this email wasn't found"));
        Hibernate.initialize(user.getManagedStores());
        var stores = user.getManagedStores();

        return user;
    }

    //get AllUser
    public List<User> getAllUsers() {return userRepository.findAll();}

    public boolean isUSerStoreManager(String email) {
        return userRepository.findByEmail(email)
                .map(user -> "STORE_MANAGER".equals(user.getRole().name()))
                .orElse(false);
    }

    public boolean isManagerOfStore(String email, Long storeId) {
        return userRepository.findByEmail(email)
                .map(user -> user.getManagedStores().stream()
                        .anyMatch(store -> store.getId().equals(storeId)))
                .orElse(false);
    }

    //Get the stores managed by a user
    public Set<Store> getManagedStores(String email) {
        return userRepository.findByEmail(email)
                .map(User::getManagedStores)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));
    }

//    //Add store to the stores managed by a user, by user id and store id
//    @Transactional
//    public User addManagedStore(Long userId, Long storeId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));
//
//        Hibernate.initialize(user.getManagedStores());
//
//        Store store = storeRepository.findById(storeId)
//                .orElseThrow(()-> new ResourceNotFoundException("Store not found!"));
//
//        Hibernate.initialize(store.getStoreInventories());
//
//        user.addManagedStore(store);
//
//        return userRepository.save(user);
//    }

    //remove a store from the stores managed by a user, referenced by user ID and store ID
    public User removeManagedStore(Long userId, Long storeId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.removeManagedStore(storeRepository.findById(storeId)
                            .orElseThrow(()-> new ResourceNotFoundException("Store not found!")));
                    return userRepository.save(user);
                })
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }
    public void addManegedStore(String email, Store store) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.addManagedStore(store);
                    userRepository.save(user);
                });
    }
}
