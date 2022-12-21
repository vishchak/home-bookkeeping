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
import com.vaadin.flow.router.Route;


import java.util.Date;

import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.amountField;
import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.textFiled;

//dialog filed add transaction
//ADD ACCOUNT REPO METHOD FIND CURRENT ACCOUNT BY USER AND NAME EVENTUALLY
@Route("add-transaction")
public class DialogField extends Div {
    Dialog dialog = new Dialog();
    Binder<Transaction> binder = new BeanValidationBinder<>(Transaction.class);
    TextField note = textFiled("Note");
    NumberField transactionAmount = amountField("Amount");

    ComboBox<Category> category = new ComboBox<>("Category");

    ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");

    CurrentUserServiceImpl currentUserService;
    AccountServiceImpl accountService;
    TransactionServiceImpl transactionService;
    CategoryServiceImpl categoryService;
    SubcategoryServiceImpl subcategoryService;

    public DialogField(CurrentUserServiceImpl currentUserService,
                       AccountServiceImpl accountService,
                       TransactionServiceImpl transactionService,
                       CategoryServiceImpl categoryService,
                       SubcategoryServiceImpl subcategoryService) {
        this.currentUserService = currentUserService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;

        dialog.setHeaderTitle("Add transaction");
        dialog.setCloseOnOutsideClick(false);

        binder.bindInstanceFields(this);

        category.setItems(categoryService.findAllCategories());
        category.setItemLabelGenerator(Category::getCategoryName);

        subcategory.setItems(subcategoryService.findAllCategories());
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button addButton = createAddButton();
        addButton.addClickListener(e -> validateAndAdd());

        Button cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui ->
                ui.navigate("")));
        cancelButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(addButton);

        add(dialog);
        dialog.open();

        // Center the button within the example
        getStyle().set("position", "fixed").set("top", "0").set("right", "0")
                .set("bottom", "0").set("left", "0").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center");

    }

    private void validateAndAdd() {
        try {
            getUI().ifPresent(ui -> {
                //CHANGE ACCOUNT FIELD USING FIND CURRENT ACCOUNT METHOD
                transactionService.addTransaction(new Transaction(
                        transactionAmount.getValue(),
                        note.getValue(), new Date(),
                        accountService.findByAccountName("test account", currentUserService.findUserByEmailOrLogin("test user")),
                        category.getValue(),
                        subcategory.getValue()
                ));
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

    private static Button createAddButton() {
        Button saveButton = new Button("Add");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return saveButton;
    }
}
