package com.gmail.vishchak.denis.views.list.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class LoginViaServicesForm extends FormLayout {
    private static final String OAUTH_GOOGLE_URL = "/oauth2/authorization/google";
    private static final String OAUTH_FACEBOOK_URL = "/oauth2/authorization/facebook";

    public LoginViaServicesForm() {
        addClassNames("login-via-services");

        Button loginViaGoogle = new Button("Login with Google", e -> UI.getCurrent().getPage().open(OAUTH_GOOGLE_URL, "_self"));

        Button loginViaFacebook = new Button("Login with Facebook", e -> UI.getCurrent().getPage().open(OAUTH_FACEBOOK_URL, "_self"));

        RouterLink registerLink = new RouterLink("Don't have an account? Register", RegisterView.class);


        loginViaFacebook.addClassNames("login-via-facebook", "wide-button");
        loginViaGoogle.addClassNames("login-via-google", "wide-button");
        registerLink.addClassNames("register-link", "wide-button");

        VerticalLayout formLayout = new VerticalLayout(
                new H2("Socials"),
                loginViaFacebook,
                loginViaGoogle,
                registerLink
        );

        add(formLayout);
    }
}
