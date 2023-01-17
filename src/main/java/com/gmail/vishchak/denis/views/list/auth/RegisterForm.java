package com.gmail.vishchak.denis.views.list.auth;


import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterForm extends FormLayout {
    private final CurrentUserServiceImpl userService;
    private final PasswordEncoder encoder;
    private final TextField username = new TextField("UserName");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmation = new PasswordField("Confirm password");

    public RegisterForm(CurrentUserServiceImpl currentUserService, PasswordEncoder encoder, Runnable runnable) {
        this.userService = currentUserService;
        this.encoder = encoder;

        addClassName("login-view-register-form");

        add(
                new H2("Register"),
                username,
                password,
                confirmation,
                getButtonsLayout(runnable));
    }

    private void register(String username, String password, String confirmation, Runnable runnable) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter a username");
        } else if (userService.existsByLogin(username)) {
            Notification.show("User " + username + " already exists");
        } else if (password.isEmpty()) {
            Notification.show("Enter a password");
        } else if (!password.equals(confirmation)) {
            Notification.show("Passwords do not match");
        } else {
            userService.registerUser(new CurrentUser(username, encoder.encode(password), null, null));
            Notification.show("Successfully registered");
            runnable.run();
            setVisible(false);
        }
    }

    private Component getButtonsLayout(Runnable runnable) {
        Button registerButton = new Button("Confirm", e -> register
                (
                        username.getValue(),
                        password.getValue(),
                        confirmation.getValue(),
                        runnable
                )
        );
        registerButton.addClassNames("button--primary", "register-wide-button");

        Button cancelButton = new Button("Cancel", e -> {
            runnable.run();
            setVisible(false);
        });
        cancelButton.addClassNames("button--secondary", "register-wide-button");

        return new HorizontalLayout(cancelButton, registerButton);
    }
}
