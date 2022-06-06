package com.example.bookstore.security.controllers;

import com.example.bookstore.models.User;
import com.example.bookstore.security.models.Role;
import com.example.bookstore.security.services.RoleService;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @GetMapping("/security/userEdit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", roleService.getUserRoles(user));
        model.addAttribute("userNotAssignedRoles", roleService.getUserNotAssignedRoles(user));
        return "/admin/user-edit";
    }

    @RequestMapping("/security/role/assign/{userId}/{roleId}")
    public RedirectView assignRole(@PathVariable Integer userId,
                             @PathVariable Integer roleId, RedirectAttributes redirectAttributes) {
        String message = roleService.assignRole(userId, roleId);
        RedirectView redirectView = new RedirectView("/security/userEdit/"+userId, true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("/security/role/unassign/{userId}/{roleId}")
    public RedirectView unassignRole(@PathVariable Integer userId,
                               @PathVariable Integer roleId, RedirectAttributes redirectAttributes) {
        String message = roleService.unassignRole(userId, roleId);
        RedirectView redirectView = new RedirectView("/security/userEdit/"+userId, true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

}
