package com.gmail.vishchak.denis.views.list.auth;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route("login")
@PageTitle("Login | MoneyLonger")
public class LoginView extends VerticalLayout implements BeforeEnterListener {

    private final LoginForm login = new LoginForm();


    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(
                new H1("MoneyLonger"),
                login,
                new RouterLink("register", RegisterView.class)
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
}