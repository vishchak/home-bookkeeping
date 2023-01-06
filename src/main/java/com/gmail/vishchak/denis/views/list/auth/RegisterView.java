package com.gmail.vishchak.denis.views.list.auth;


import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;


@Route("register")
@AnonymousAllowed
public class RegisterView extends Composite {

    private final CurrentUserServiceImpl currentUserService;
    private final PasswordEncoder encoder;

    public RegisterView(CurrentUserServiceImpl currentUserService, PasswordEncoder encoder) {
        this.currentUserService = currentUserService;
        this.encoder = encoder;
    }

    @Override
    protected Component initContent() {
        TextField username = new TextField("UserName");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmation = new PasswordField("Confirm password");
        return new VerticalLayout(
                new H2("Register"),
                username,
                password,
                confirmation,
                new Button("Send", e -> register(
                        username.getValue(),
                        password.getValue(),
                        confirmation.getValue()
                ))
        );
    }

    private void register(String username, String password, String confirmation) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter a username");
        } else if (currentUserService.existsByLogin(username)) {
            Notification.show("User " + username + " exists");
        } else if (password.isEmpty()) {
            Notification.show("Enter a password");
        } else if (!password.equals(confirmation)) {
            Notification.show("Passwords dont match");
        } else {
            currentUserService.registerUser(username, encoder.encode(password));
            UI.getCurrent().navigate("");
            Notification.show("Successfully registered");
        }
    }
}
