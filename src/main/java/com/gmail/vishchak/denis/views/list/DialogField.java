package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.service.*;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;


import java.util.Date;
import java.util.Optional;

import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.amountField;
import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.textFiled;

//dialog filed add transaction
//ADD ACCOUNT REPO METHOD FIND CURRENT ACCOUNT BY USER AND NAME EVENTUALLY
@Route("add-transaction")
@PageTitle("Transaction")
public class DialogField extends Div implements HasUrlParameter<Long> {
    private final AccountServiceImpl accountService;
    private final TransactionServiceImpl transactionService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final Dialog dialog = new Dialog();
    private final Binder<Transaction> binder = new BeanValidationBinder<>(Transaction.class);
    private final TextField note = textFiled("Note");
    private final  NumberField transactionAmount = amountField("Amount");

    private final ComboBox<Category> category = new ComboBox<>("Category");

    private final  ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");

    public DialogField(AccountServiceImpl accountService,
                       TransactionServiceImpl transactionService,
                       CategoryServiceImpl categoryService,
                       SubcategoryServiceImpl subcategoryService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter Long id) {
        if (id == null) {
            dialogCreate("Add transaction", null, "add");
            return;
        }
        dialogCreate("Update transaction", id, "update");
    }

    private void dialogCreate(String header, Long id, String buttonText) {
        dialog.setHeaderTitle(header);
        dialog.setCloseOnOutsideClick(false);

        binder.bindInstanceFields(this);

        labelGenerator();

        if (id != null) {
            Optional<Transaction> optionalTransaction = transactionService.findById(id);
            optionalTransaction.ifPresent(transaction -> {
                category.setValue(transaction.getCategory());
                subcategory.setValue(transaction.getSubcategory());
                note.setValue(transaction.getNote());
                transactionAmount.setValue(transaction.getTransactionAmount());
            });
        }

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button confirmButton = createConfirmButton(buttonText);
        if (id == null) {
            confirmButton.addClickListener(e -> validateAndAdd());
        } else {
            confirmButton.addClickListener(e -> validateAndUpdate(id));
        }

        Button cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate("")));
        cancelButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(new HorizontalLayout(cancelButton, confirmButton));

        add(dialog);
        dialog.open();
    }

    private void labelGenerator() {
        subcategory.setEnabled(false);

        category.setItems(categoryService.findAllCategories());
        category.setItemLabelGenerator(Category::getCategoryName);
        category.addValueChangeListener(event -> {
            subcategory.setEnabled(!category.isEmpty());
            subcategory.setItems(subcategoryService.findByCategory(category.getValue()));
            subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);
        });
    }

    private void validateAndAdd() {
        try {
            getUI().ifPresent(ui -> {
                //CHANGE ACCOUNT FIELD USING FIND CURRENT ACCOUNT METHOD
                transactionService.addTransaction(new Transaction(
                        transactionAmount.getValue(),
                        note.getValue(), new Date(),
                        accountService.findByAccountId(1L).get(),
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

    private void ErrorNotification() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        HorizontalLayout layout = new HorizontalLayout(new Div(new Text("Fill all the necessary fields!")));

        notification.setDuration(3000);
        notification.add(layout);
        notification.open();
    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(transactionAmount, note, category, subcategory);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private static Button createConfirmButton(String buttonText) {
        Button saveButton = new Button(buttonText);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return saveButton;
    }
}
