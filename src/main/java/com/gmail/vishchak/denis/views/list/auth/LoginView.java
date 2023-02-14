package com.gmail.vishchak.denis.views.list.auth;

import com.gmail.vishchak.denis.service.CustomUserServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("login")
@PageTitle("Login | FROG-STOCK")
@CssImport("./themes/frog-stock/components/login/login-view.css")
public class LoginView extends Composite<VerticalLayout> implements BeforeEnterListener {
    private static final String OAUTH_GOOGLE_URL = "/oauth2/authorization/google";
    private static final String OAUTH_FACEBOOK_URL = "/oauth2/authorization/facebook";
    private static final String LOGO_URL = "/images/frog-stock.png";
    private final LoginForm loginForm = new LoginForm();
    private final RegisterForm registerForm;
    private final Button signUpOfferButton = new Button("Don't have an account? Sing up", e -> updatePage());
    private final Button signInButton = new Button("Have an account? Log in", e -> updatePage());

    public LoginView(CustomUserServiceImpl userService, PasswordEncoder encoder) {
        this.registerForm = new RegisterForm(userService, encoder, this::updatePage);

        VerticalLayout loginView = getContent();
        loginView.setSizeFull();

        loginView.addClassName("login-view");

        configureLoginView();
        configureLoginForm();

        loginView.add(getPageContent());
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
        signUpOfferButton.addClassNames("button--primary", "login-width");
        signInButton.addClassNames("button--primary", "login-width");

        signInButton.setVisible(false);
        registerForm.setVisible(false);

        loginForm.addClassName("login-view-login-form");
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
    }

    private void configureLoginForm() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("FROG-STOCK account");

        loginForm.setI18n(i18n);
    }

    private Component getPageContent() {
        VerticalLayout content = new VerticalLayout
                (
                        getLogoLayout(),
                        getLoginViaServicesLayout(),
                        getFormsLayout()
                );

        content.addClassName("login-view-content");

        return content;
    }

    private Component getLogoLayout() {
        Image logo = new Image(LOGO_URL, "FROG-STOCK logo");
        logo.addClassName("login-view-logo");

        HorizontalLayout logoLayout = new HorizontalLayout(new H1("FROG-STOCK"), logo);
        logoLayout.addClassName("login-view-logo-layout");

        return logoLayout;
    }

    private Component getLoginViaServicesLayout() {
        Button loginViaGoogle = new Button("Log in with Google", e -> UI.getCurrent().getPage().open(OAUTH_GOOGLE_URL, "_self"));

        Button loginViaFacebook = new Button("Log in with Facebook", e -> UI.getCurrent().getPage().open(OAUTH_FACEBOOK_URL, "_self"));

        loginViaFacebook.addClassNames("button--secondary", "login-width");
        loginViaGoogle.addClassNames("button--tertiary", "login-width");

        return new VerticalLayout
                (
                        loginViaFacebook,
                        loginViaGoogle

                );
    }

    private Component getFormsLayout() {
        return new VerticalLayout
                (
                        loginForm,
                        registerForm,
                        signUpOfferButton,
                        signInButton
                );
    }

    private void updatePage() {
        loginForm.setVisible(!loginForm.isVisible());
        registerForm.setVisible(!loginForm.isVisible());

        if (loginForm.isVisible()) {
            signUpOfferButton.setVisible(true);
            signInButton.setVisible(false);
        } else {
            signInButton.setVisible(true);
            signUpOfferButton.setVisible(false);
        }
    }
}