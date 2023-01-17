package com.gmail.vishchak.denis.views.list.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class LoginViaServicesForm extends FormLayout {
    private static final String OAUTH_GOOGLE_URL = "/oauth2/authorization/google";
    private static final String OAUTH_FACEBOOK_URL = "/oauth2/authorization/facebook";

    public LoginViaServicesForm() {
        Button loginViaGoogle = new Button("Login with Google", e -> UI.getCurrent().getPage().open(OAUTH_GOOGLE_URL, "_self"));

        Button loginViaFacebook = new Button("Login with Facebook", e -> UI.getCurrent().getPage().open(OAUTH_FACEBOOK_URL, "_self"));

        loginViaFacebook.addClassNames("button--secondary", "wide-button");
        loginViaGoogle.addClassNames("button--secondary", "wide-button");

        VerticalLayout formLayout = new VerticalLayout(
                new H2("Socials"),
                loginViaFacebook,
                loginViaGoogle
        );

        add(formLayout);
    }
}
