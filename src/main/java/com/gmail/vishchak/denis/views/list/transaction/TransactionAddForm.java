package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.service.CategoryServiceImpl;
import com.gmail.vishchak.denis.service.SubcategoryServiceImpl;
import com.gmail.vishchak.denis.service.TransactionServiceImpl;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.amountField;
import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.textFiled;

@PermitAll
@Route("add-transaction")
@PageTitle("Transaction | FROG-STOCK")
@CssImport("./themes/flowcrmtutorial/components/form/transaction-goal-form.css")
public class TransactionAddForm extends Composite<VerticalLayout> implements HasUrlParameter<Long> {
    private final AccountServiceImpl accountService;
    private final TransactionServiceImpl transactionService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TextField note = textFiled("Note");
    private final NumberField transactionAmount = amountField("Amount");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final ComboBox<Account> accountComboBox = new ComboBox<>("Account");
    private final CustomUser user;

    public TransactionAddForm(AccountServiceImpl accountService, TransactionServiceImpl transactionService, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, SecurityService securityService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.user = securityService.getAuthenticatedUser();

        Binder<Transaction> binder = new BeanValidationBinder<>(Transaction.class);
        binder.bindInstanceFields(this);

        labelGenerator();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter Long transactionId) {
        if (transactionId == null) {
            formCreate(null, "Add transaction");
        } else {
            checkUser(transactionId);
            formCreate(transactionId, "Edit transaction");
        }
    }

    private void checkUser(Long goalId) {
        transactionService.findById(goalId).ifPresent(t -> {
            if (!Objects.equals(user.getUserId(), t.getAccount().getUser().getUserId())) {
                Notification.show("Access denied!", 3000, Notification.Position.BOTTOM_START);
                UI.getCurrent().getPage().open("goals", "_self");
            }
        });
    }

    private void formCreate(Long transactionId, String header) {
        if (transactionId != null) {
            configureUpdateTransaction(transactionId);
        }

        VerticalLayout content = new VerticalLayout
                (
                        new H2(header),
                        transactionAmount,
                        note,
                        accountComboBox,
                        category,
                        subcategory,
                        SharedComponents.createButtonsLayout
                                (
                                        transactionId,
                                        "",
                                        this::validateAndAdd,
                                        (id) -> validateAndUpdate(transactionId),
                                        UI.getCurrent()
                                )

                );
        content.addClassName("transaction-goal-form-content");

        VerticalLayout layout = getContent();

        layout.add(content);
        layout.addClassName("transaction-goal-form-layout");
        layout.setSizeFull();
    }

    private void configureUpdateTransaction(Long transactionId) {
        transactionService.findById(transactionId).ifPresent
                (transaction ->
                        {
                            category.setValue(transaction.getCategory());
                            subcategory.setValue(transaction.getSubcategory());
                            note.setValue(transaction.getNote());
                            transactionAmount.setValue(transaction.getTransactionAmount());
                        }
                );
        accountComboBox.setVisible(false);
    }

    private void labelGenerator() {
        subcategory.setEnabled(false);

        accountComboBox.setItems(accountService.findAccountsByUser(user));
        accountComboBox.setItemLabelGenerator(Account::getAccountName);
        accountComboBox.setRequired(true);

        category.setItems(categoryService.findAllCategories());
        category.setItemLabelGenerator(Category::getCategoryName);

        category.addValueChangeListener
                (
                        event -> {
                            subcategory.setEnabled(!category.isEmpty());
                            List<Subcategory> subcategoryList = subcategoryService.findByCategory(category.getValue());
                            subcategory.setItems(subcategoryList);
                            subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);
                        }
                );
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
            Notification.show("Fill all the necessary fields", 3000, Notification.Position.BOTTOM_START);
        }
    }

    private void validateAndUpdate(Long id) {
        try {
            getUI().ifPresent(ui -> {
                transactionService.updateTransaction
                        (
                                id,
                                transactionAmount.getValue(),
                                note.getValue(),
                                category.getValue(),
                                subcategory.getValue()
                        );
                ui.navigate("");
            });
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            Notification.show("Fill all the necessary fields", 3000, Notification.Position.BOTTOM_START);
        }
    }
}
