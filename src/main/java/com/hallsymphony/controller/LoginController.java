package com.hallsymphony.controller;

import com.hallsymphony.model.user.User;
import com.hallsymphony.service.UserService;

public class LoginController {
    private final UserService userService = new UserService();

    public User handleLogin(String email, String password) {
        return userService.authenticateUser(email, password);
    }

    public void redirectUserByRole(User user) {
        // TODO: Redirect based on user role (e.g., to dashboards)
    }
}
