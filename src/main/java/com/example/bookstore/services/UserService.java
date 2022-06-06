package com.example.bookstore.services;

import com.example.bookstore.models.User;
import com.example.bookstore.repositories.UserRepository;
import com.example.bookstore.security.models.Role;
import com.example.bookstore.security.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public User getCurrentLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username);
    }

    // Return the list of users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Add a new user
    public String save(User user) {
        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
        user.setUsername(user.getUsername().trim());

        if (user.getFirstName().length() < 3 || user.getFirstName().length() > 100) {
            return "Imię musi zawierać od 3 do 100 znaków";
        }
        if (user.getLastName().length() < 3 || user.getLastName().length() > 100) {
            return "Nazwisko musi zawierać od 3 do 100 znaków";
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 100) {
            return "Nazwa użytkownika musi zawierać od 3 do 100 znaków";
        }
        if (user.getPassword().length() < 16 || user.getPassword().length() > 255) {
            return "Hasło musi zawierać od 16 do 255 znaków";
        }

        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (user.getUsername().equals(u.getUsername())) {
                return "Taka nazwa użytkownika już istnieje";
            }
        }

        user.setPassword(encoder.encode(user.getPassword()));
        Role role = roleRepository.findById(2).orElse(null);
        Set<Role> userRoles = user.getRoles();
        userRoles.add(role);
        user.setRoles(userRoles);
        userRepository.save(user);

        return "Zarejestrowano pomyślnie";
    }

    // Get by ID
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // Delete by ID
    public String delete(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            Set<Role> userRoles = user.getRoles();

            for (Role role : userRoles) {
                if (role.getName().equals("Admin")) {
                    return "Nie można usunąć konta administratora";
                }
            }

            user.setRoles(null);
            user.setBooks2(null);
            user.setOrders(null);
            userRepository.save(user);
            userRepository.deleteById(id);

            return "Usunięto użytkownika";
        }
        return "Taki użytkownik nie istnieje";
    }

    public String tryToChangePassword(User user, String oldPassword, String newPassword) {

        if (newPassword.length() < 16 || newPassword.length() > 255) {
            return "Nowe hasło musi zawierać od 16 do 255 znaków";
        }

        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            return "Hasło zostało zmienione";
        }

        return "Wprowadzone stare hasło jest błędne";

    }

    public String tryToChangeUsername(User user, String newUsername, String password) {

        if (newUsername.length() < 3 || newUsername.length() > 100) {
            return "Nowe nazwa użytkownika musi zawierać od 3 do 100 znaków";
        }

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            user.setUsername(newUsername);
            userRepository.save(user);
            return "Nazwa użytkownika została zmieniona";
        }

        return "Wprowadzone hasło jest błędne";

    }

    public String tryToChangeName(User user, String newFirstName, String newLastName, String password) {

        if (newFirstName.length() < 3 || newFirstName.length() > 100) {
            return "Nowe imię musi zawierać od 3 do 100 znaków";
        }

        if (newLastName.length() < 3 || newLastName.length() > 100) {
            return "Nowe nazwisko musi zawierać od 3 do 100 znaków";
        }

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            userRepository.save(user);
            return "Dane zostały zmienione";
        }

        return "Wprowadzone hasło jest błędne";

    }

    public String deleteAccount() {
        User user = getCurrentLoggedInUser();
        Set<Role> userRoles = user.getRoles();

        for (Role role : userRoles) {
            if (role.getName().equals("Admin")) {
                return "Nie można usunąć konta administratora";
            }
        }

        user.setRoles(null);
        user.setBooks2(null);
        user.setOrders(null);
        userRepository.save(user);
        userRepository.delete(user);

        return "Konto zostało usunięte";
    }
}
