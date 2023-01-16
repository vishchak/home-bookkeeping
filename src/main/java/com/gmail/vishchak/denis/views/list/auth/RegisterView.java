package com.gmail.vishchak.denis.views.list.auth;


import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    private static final String LOGO_URL = "/images/bankroll-froggo.png";
    private final CurrentUserServiceImpl currentUserService;
    private final PasswordEncoder encoder;
    private final TextField username = new TextField("UserName");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmation = new PasswordField("Confirm password");

    public RegisterView(CurrentUserServiceImpl currentUserService, PasswordEncoder encoder) {
        this.currentUserService = currentUserService;
        this.encoder = encoder;

        setSizeFull();
        addClassName("register-view");

        Image logo = new Image(LOGO_URL, "Bankroll Froggo logo");
        logo.addClassName("login-logo");


        VerticalLayout fieldsLayout = new VerticalLayout(
                username,
                password,
                confirmation);
        fieldsLayout.addClassName("register-fields-layout");

        fieldsLayout.setWidth("20em");

        add(
                logo,
                fieldsLayout,
                getButtonsLayout()
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
            currentUserService.registerUser(new CurrentUser(username, encoder.encode(password), null, null));
            UI.getCurrent().navigate("");
            Notification.show("Successfully registered");
        }
    }

    private Component getButtonsLayout() {
        Button registerButton = new Button("Register", e -> register(
                username.getValue(),
                password.getValue(),
                confirmation.getValue()));
        Button cancelButton = new Button("Cancel", e -> UI.getCurrent().navigate("/login"));

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, registerButton);
        buttonLayout.addClassName("register-button-layout");

        return buttonLayout;
    }
}
