package com.example.bookstore.controllers;

import com.example.bookstore.models.User;
import com.example.bookstore.security.services.RoleService;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/admin/users")
    public String getUsers(Model model) {

        List<User> userList = userService.getUsers();
        model.addAttribute("users", userList);

        return "admin/user";
    }

    @PostMapping("/users/addNew")
    public RedirectView addNew(User user, RedirectAttributes redirectAttributes) {
        String message = userService.save(user);
        String url = "/login";
        if (!message.equals("Zarejestrowano pomyślnie")) {
            url = "/register";
        }
        RedirectView redirectView = new RedirectView(url, true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @GetMapping("/users/profile")
    public String getProfile(Model model) {
        User user = userService.getCurrentLoggedInUser();
        model.addAttribute("user", user);

        return "profile";
    }

    @GetMapping("/users/changePassword")
    public String getChangePassword() {
        return "change-password";
    }

    @GetMapping("/admin/users/changeUsername")
    public String getChangeUsername() {
        return "admin/change-username";
    }

    @GetMapping("/admin/users/changeName")
    public String getChangeName() {
        return "admin/change-name";
    }

    @GetMapping("/users/deleteAccount")
    public String getDeleteAccount() {
        return "delete-account";
    }

    @PostMapping("/users/changePassword")
    public RedirectView changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, RedirectAttributes redirectAttributes) {

        User user = userService.getCurrentLoggedInUser();
        String message = userService.tryToChangePassword(user, oldPassword, newPassword);

        RedirectView redirectView = new RedirectView("/users/change-password", true);
        redirectAttributes.addFlashAttribute("message", message);

        return redirectView;
    }

    @PostMapping("/admin/users/changeUsername")
    public RedirectView changeUsername(@RequestParam("newUsername") String newUsername, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentLoggedInUser();
        String url = "/logout";
        String message = userService.tryToChangeUsername(user, newUsername, password);
        if (message.equals("Nazwa użytkownika została zmieniona")) {
            url = "/admin/users/change-username";
        }

        RedirectView redirectView = new RedirectView(url, true);
        redirectAttributes.addFlashAttribute("message", message);

        return redirectView;
    }

    @PostMapping("/admin/users/changeName")
    public RedirectView changeName(@RequestParam("newFirstName") String newFirstName, @RequestParam("newLastName") String newLastName, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentLoggedInUser();
        String url = "/logout";
        String message = userService.tryToChangeName(user, newFirstName, newLastName, password);
        if (message.equals("Dane zostały zmienione")) {
            url = "/admin/users/change-name";
        }

        RedirectView redirectView = new RedirectView(url, true);
        redirectAttributes.addFlashAttribute("message", message);

        return redirectView;
    }

    @RequestMapping("/admin/users/findById")
    @ResponseBody
    public User findById(Integer id) {
        return userService.findById(id);
    }

    @RequestMapping(value="/admin/users/update", method={RequestMethod.PUT, RequestMethod.GET})
    public String update(User user) {

        userService.save(user);
        return "redirect:/admin/users";

    }

    @RequestMapping(value="/admin/users/delete", method={RequestMethod.DELETE, RequestMethod.GET})
    public String delete(Integer id) {

        userService.delete(id);
        return "redirect:/admin/users";

    }

    @PostMapping("/users/deleteAccount")
    public String deleteAccount(@RequestParam("password") String password) {

        userService.deleteAccount();
        return "redirect:/users/delete-account";

    }

}
