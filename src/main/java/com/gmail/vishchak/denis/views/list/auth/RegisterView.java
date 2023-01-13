package com.gmail.vishchak.denis.views.list.auth;


import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
@AnonymousAllowed
public class RegisterView extends Composite<VerticalLayout> {

    private final CurrentUserServiceImpl currentUserService;
    private final PasswordEncoder encoder;

    public RegisterView(CurrentUserServiceImpl currentUserService, PasswordEncoder encoder) {
        this.currentUserService = currentUserService;
        this.encoder = encoder;

        TextField username = new TextField("UserName");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmation = new PasswordField("Confirm password");

        VerticalLayout layout = getContent();
        layout.add(
                new H2("MoneyLonger"),
                username,
                password,
                confirmation,
                new Button("Register", e -> register(
                        username.getValue(),
                        password.getValue(),
                        confirmation.getValue()
                ))
        );

        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
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
            currentUserService.registerUser(new CurrentUser(username, encoder.encode(password), null));
            UI.getCurrent().navigate("");
            Notification.show("Successfully registered");
        }
    }
}
