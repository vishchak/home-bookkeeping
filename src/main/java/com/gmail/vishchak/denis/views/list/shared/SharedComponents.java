package com.gmail.vishchak.denis.views.list.shared;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class SharedComponents {
    public static DatePicker dateField(String format, String label) {
        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat(format);

        DatePicker date = new DatePicker();
        date.setLabel(label);
        date.setI18n(singleFormatI18n);

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        date.setMax(now);
        date.setSizeFull();

        return date;
    }

    public static NumberField amountField(String label) {
        NumberField dollarField = new NumberField();
        dollarField.setLabel(label);
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        dollarField.setSizeFull();
        dollarField.setClearButtonVisible(true);

        return dollarField;
    }

    public static TextField textFiled(String value) {
        TextField textField = new TextField();
        textField.setLabel(value);
        textField.setClearButtonVisible(true);
        textField.setSizeFull();

        return textField;
    }

    public static void configureDialog(Dialog dialog, Button deleteButton) {
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);

        dialog.open();
    }

    //replace with currentUser id after security
    public static Component getAccountField(ComboBox<Account> accountComboBox, AccountServiceImpl accountService) {
        List<Account> accounts = accountService.findAccountsByUserId(1L);

        accountComboBox.setItems(accounts);
        accountComboBox.setItemLabelGenerator(Account::getAccountName);
        accountComboBox.setHelperText("Choose account");


        return accountComboBox;
    }

    public static Button getAddComponentButton(String buttonName, String url) {
        Button addTransactionButton = new Button(buttonName);
        addTransactionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTransactionButton.setIcon(new Icon("lumo", "plus"));

        addTransactionButton.addClickListener(e -> addTransactionButton.getUI().ifPresent(ui ->
                ui.navigate(url)));
        return addTransactionButton;
    }

    public static Button createConfirmButton(String buttonText) {
        Button saveButton = new Button(buttonText);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return saveButton;
    }

    public static void ErrorNotification() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        HorizontalLayout layout = new HorizontalLayout(new Div(new Text("Fill all the necessary fields!")));

        notification.setDuration(3000);
        notification.add(layout);
        notification.open();
    }
}
