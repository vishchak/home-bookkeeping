package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.ErrorNotification;

@PermitAll
@Route("add-account")
@PageTitle("Add account | | FROG-STOCK")
@CssImport("./themes/flowcrmtutorial/components/form/create-account-form.css")
public class AccountCreateForm extends Composite<VerticalLayout> {
    private final AccountServiceImpl accountService;
    private final TextField accountName = SharedComponents.textFiled("Enter account's name");
    private final NumberField accountAmount = SharedComponents.amountField("Enter account's amount");
    private final CustomUser user;

    public AccountCreateForm(AccountServiceImpl accountService, SecurityService securityService) {
        this.accountService = accountService;
        this.user = securityService.getAuthenticatedUser();

        Binder<Account> binder = new BeanValidationBinder<>(Account.class);
        binder.bindInstanceFields(this);


        VerticalLayout content = new VerticalLayout
                (
                        new H2("Create an account"),
                        createFormLayout(),
                        createButtons()
                );
        content.addClassName("create-account-dialog-content");

        VerticalLayout layout = getContent();

        layout.addClassName("create-account-dialog-layout");
        layout.add(content);
        layout.setSizeFull();
    }

    private Component createButtons() {
        String buttonWidthClassName = "create-account-dialog-button-width";

        Button confirmButton = new Button("Add", e -> validateAndAdd(accountName.getValue(), accountAmount.getValue()));
        confirmButton.addClassNames(buttonWidthClassName);

        Button cancelButton = new Button("Cancel", e -> UI.getCurrent().navigate(TransactionView.class));

        if (accountService.findAccountsByUser(user).isEmpty()) {
            cancelButton.setEnabled(false);
            cancelButton.addClassNames("button--disabled", buttonWidthClassName);
        } else {
            cancelButton.addClassNames("button--primary", buttonWidthClassName);
        }

        return new HorizontalLayout(cancelButton, confirmButton);
    }

    private Component createFormLayout() {
        accountName.setRequired(true);
        accountAmount.setRequiredIndicatorVisible(true);

        return new VerticalLayout(accountName, accountAmount);
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
