package com.gmail.vishchak.denis.views.list.auth;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Bankroll Froggo")
public class LoginView extends VerticalLayout implements BeforeEnterListener {
    private static final String LOGO_URL = "/images/bankroll-froggo.png";
    private final LoginForm login = new LoginForm();
    private final LoginViaServicesForm loginViaServicesForm = new LoginViaServicesForm();

    public LoginView() {
        setSizeFull();
        addClassName("login-view");

        login.addClassName("login-form");
        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");

        Image logo = new Image(LOGO_URL, "Bankroll Froggo logo");
        logo.addClassName("login-logo");

        HorizontalLayout logoLayout = new HorizontalLayout(new H1("Bankroll Froggo"), logo);
        logoLayout.addClassName("login-logo-layout");

        add(
                logoLayout,
                addContent()
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }

    private Component addContent() {
        HorizontalLayout content = new HorizontalLayout(loginViaServicesForm, login);

        content.addClassName("login-content");

        content.setAlignItems(Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return content;
    }
}