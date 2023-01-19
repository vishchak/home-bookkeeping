package com.gmail.vishchak.denis.views.list.auth;


import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.service.CustomUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterForm extends VerticalLayout {
    private static final String CONTENT_WIDTH = "login-width";
    private final CustomUserServiceImpl userService;
    private final PasswordEncoder encoder;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmation = new PasswordField("Confirm password");

    public RegisterForm(CustomUserServiceImpl currentUserService, PasswordEncoder encoder, Runnable runnable) {
        this.userService = currentUserService;
        this.encoder = encoder;

        username.addClassName(CONTENT_WIDTH);
        password.addClassName(CONTENT_WIDTH);
        confirmation.addClassName(CONTENT_WIDTH);

        add
                (
                        username,
                        password,
                        confirmation,
                        getSingUpButton(runnable)
                );
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
            userService.registerUser(new CustomUser(username, encoder.encode(password), null, null));
            Notification.show("Successfully registered");
            runnable.run();
            setVisible(false);
        }
    }


    private Component getSingUpButton(Runnable runnable) {
        Button signUpButton = new Button("Sign up", e -> register
                (
                        username.getValue(),
                        password.getValue(),
                        confirmation.getValue(),
                        runnable
                )
        );
        signUpButton.addClassNames("login-width");

        return signUpButton;
    }
}
