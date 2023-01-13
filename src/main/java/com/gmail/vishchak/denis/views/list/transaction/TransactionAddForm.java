package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;


import javax.annotation.security.PermitAll;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.*;


@Route("add-transaction")
@PageTitle("Transaction")
@PermitAll
public class TransactionAddForm extends Composite<VerticalLayout> implements HasUrlParameter<Long> {
    private final AccountServiceImpl accountService;
    private final TransactionServiceImpl transactionService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final Binder<Transaction> binder = new BeanValidationBinder<>(Transaction.class);
    private final TextField note = textFiled("Note");
    private final NumberField transactionAmount = amountField("Amount");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final ComboBox<Account> accountComboBox = new ComboBox<>("Account");
    private final CurrentUser user;

    public TransactionAddForm(AccountServiceImpl accountService, TransactionServiceImpl transactionService, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, SecurityService securityService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.user = securityService.getAuthenticatedUser();

        labelGenerator();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter Long id) {
        if (id == null) {
            formCreate("Add transaction", null, "add");
            return;
        }
        formCreate("Update transaction", id, "update");
    }

    private void formCreate(String header, Long id, String buttonText) {

        binder.bindInstanceFields(this);

        VerticalLayout layout = getContent();

        layout.add(
                new H2(header),
                createFormLayout(),
                createButtons(id, buttonText)
        );

        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        if (id != null) {
            accountComboBox.setVisible(false);
            Optional<Transaction> optionalTransaction = transactionService.findById(id);
            optionalTransaction.ifPresent(transaction -> {
                category.setValue(transaction.getCategory());
                subcategory.setValue(transaction.getSubcategory());
                note.setValue(transaction.getNote());
                transactionAmount.setValue(transaction.getTransactionAmount());
            });
        }
    }

    private HorizontalLayout createButtons(Long id, String buttonText) {
        Button confirmButton = createConfirmButton(buttonText);
        if (id == null) {
            confirmButton.addClickListener(e -> validateAndAdd());
        } else {
            confirmButton.addClickListener(e -> validateAndUpdate(id));
        }

        Button cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate("")));
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.getStyle().set("margin-right", "auto");

        return new HorizontalLayout(cancelButton, confirmButton);
    }

    private void labelGenerator() {
        subcategory.setEnabled(false);

        accountComboBox.setItems(accountService.findAccountsByUser(user));
        accountComboBox.setItemLabelGenerator(Account::getAccountName);
        accountComboBox.setRequired(true);

        category.setItems(categoryService.findAllCategories());
        category.setItemLabelGenerator(Category::getCategoryName);
        category.addValueChangeListener(event -> {
            subcategory.setEnabled(!category.isEmpty());
            List<Subcategory> subcategoryList = subcategoryService.findByCategory(category.getValue());
            subcategoryList.forEach(s -> {
                if (s.getSubcategoryName().equalsIgnoreCase("goal")) {
                    subcategoryList.remove(s);
                }
            });
            subcategory.setItems(subcategoryList);
            subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);
        });
    }


    private void validateAndAdd() {
        try {
            getUI().ifPresent(ui -> {
                transactionService.addTransaction(new Transaction(
                        transactionAmount.getValue(),
                        note.getValue(), new Date(),
                        accountComboBox.getValue(),
                        category.getValue(),
                        subcategory.getValue()
                ));
                ui.navigate("");
            });
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            ErrorNotification();
        }
    }

    private void validateAndUpdate(Long id) {
        try {
            getUI().ifPresent(ui -> {
                transactionService.updateTransaction(id,
                        transactionAmount.getValue(),
                        note.getValue(),
                        category.getValue(),
                        subcategory.getValue()
                );
                ui.navigate("");
            });
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            ErrorNotification();
        }
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(transactionAmount, note, accountComboBox, category, subcategory);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}
