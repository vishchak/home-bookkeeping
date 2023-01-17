package com.gmail.vishchak.denis.views.list.auth;

import com.gmail.vishchak.denis.service.CurrentUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("login")
@PageTitle("Login | FROG-STOCK")
public class LoginView extends VerticalLayout implements BeforeEnterListener {
    private static final String LOGO_URL = "/images/frog-stock.png";
    private final LoginForm loginForm = new LoginForm();
    private final LoginViaServicesForm loginViaServicesForm = new LoginViaServicesForm();
    private final RegisterForm registerForm;

    public LoginView(CurrentUserServiceImpl currentUserService, PasswordEncoder encoder) {
        this.registerForm = new RegisterForm(currentUserService, encoder, () -> loginForm.setVisible(true));

        setSizeFull();
        addClassName("login-view");

        configureLoginView();

        add(
                getLogoLayout(),
                addContent()
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }

    private void configureLoginView() {
        registerForm.setVisible(false);

        loginForm.addClassName("login-view-login-form");
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
    }

    private Component getLogoLayout() {
        Image logo = new Image(LOGO_URL, "FROG-STOCK logo");
        logo.addClassName("login-view-logo");

        HorizontalLayout logoLayout = new HorizontalLayout(new H1("FROG-STOCK"), logo);
        logoLayout.addClassName("login-view-logo-layout");

        return logoLayout;
    }

    private Component addContent() {
        Button registerOfferButton = new Button("dont have an account? Register", e -> {
            loginForm.setVisible(!loginForm.isVisible());
            registerForm.setVisible(!loginForm.isVisible());
        });
        registerOfferButton.addClassNames("button--primary", "wide-button");

        VerticalLayout loginRegisterLayout = new VerticalLayout(loginViaServicesForm, registerOfferButton);
        loginRegisterLayout.addClassName("login-view-register");

        return new HorizontalLayout(loginRegisterLayout, loginForm, registerForm);
    }
}