package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;


import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.ErrorNotification;

@Route("add-account")
@PageTitle("Add account | MoneyLonger")
@PermitAll
public class AccountCreateForm extends Composite<VerticalLayout> {
    private final AccountServiceImpl accountService;
    private final TextField accountName = SharedComponents.textFiled("Enter account's name");
    private final NumberField accountAmount = SharedComponents.amountField("Enter account's amount");
    private final CurrentUser user;

    public AccountCreateForm(AccountServiceImpl accountService, CurrentUserServiceImpl userService, SecurityService securityService) {
        this.accountService = accountService;

        UserDetails userDetails = securityService.getAuthenticatedUser();
        this.user = userService.findUserByEmailOrLogin(userDetails.getUsername());


        Binder<Account> binder = new BeanValidationBinder<>(Account.class);
        binder.bindInstanceFields(this);

        VerticalLayout layout = getContent();

        layout.add(
                new H2("Create an account"),
                createFormLayout(),
                createButtons()
        );

        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    private HorizontalLayout createButtons() {
        Button confirmButton = SharedComponents.createConfirmButton("Add");
        confirmButton.addClickListener(e -> validateAndAdd(accountName.getValue(), accountAmount.getValue()));

        Button cancelButton = new Button("Cancel", e -> UI.getCurrent().navigate(TransactionView.class));

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        if (accountService.findAccountsByUser(user).isEmpty()) {
            buttonLayout.remove(cancelButton);
        }
        return buttonLayout;
    }

    private VerticalLayout createFormLayout() {
        accountName.setRequired(true);
        accountAmount.setRequiredIndicatorVisible(true);

        VerticalLayout dialogLayout = new VerticalLayout(accountName, accountAmount);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private void validateAndAdd(String accountName, Double accountAmount) {
        try {
            if (accountName.isEmpty() || accountName.isBlank()) {
                throw new NullPointerException();
            }
            accountService.addAccount(new Account(accountName, accountAmount, user));
            UI.getCurrent().navigate(TransactionView.class);
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            ErrorNotification();
        }
    }
}
