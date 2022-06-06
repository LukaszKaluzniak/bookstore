package com.example.bookstore.security.services;

import com.example.bookstore.models.User;
import com.example.bookstore.repositories.UserRepository;
import com.example.bookstore.security.models.Role;
import com.example.bookstore.security.repositories.RoleRepository;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findById(int id) {
        return roleRepository.findById(id);
    }

    public void delete(int id) {
        roleRepository.deleteById(id);
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public String assignRole(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);

        if (user != null && role != null) {
            Set<Role> userRoles = user.getRoles();
            userRoles.add(role);
            user.setRoles(userRoles);
            userRepository.save(user);
            return "Nadano uprawnienia";
        }
        return "Nie udało się nadać uprawnień";
    }

    public String unassignRole(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);

        if (user != null && role != null) {
            if ((user == userService.getCurrentLoggedInUser() && !role.getName().equals("Admin")) || user != userService.getCurrentLoggedInUser()) {
                Set<Role> userRoles = user.getRoles();
                userRoles.removeIf(x -> x.getId().equals(roleId));
                user.setRoles(userRoles);
                userRepository.save(user);
                return "Odebrano uprawnienia";
            } else {
                return "Nie można odebrać uprawnień administratorskich samemu sobie";
            }
        }
        return "Nie udało się odebrać uprawnień";
    }

   public Set<Role> getUserRoles(User user) {
        return user.getRoles();
   }

   public Set<Role> getUserNotAssignedRoles(User user) {
        return roleRepository.getUserNotAssignedRoles(user.getId());
   }
}
