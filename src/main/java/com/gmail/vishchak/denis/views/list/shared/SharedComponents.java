package com.gmail.vishchak.denis.views.list.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Consumer;

public class SharedComponents {
    public static Component createButtonsLayout(Long id, String navigateLink, Runnable runnable, Consumer<Long> consumer, UI currentUi) {
        String buttonWidthClassName = "transaction-goal-form-button-width";

        Button confirmButton = new Button("Confirm");
        if (id == null) {
            confirmButton.addClickListener(e -> runnable.run());
        } else {
            confirmButton.addClickListener(e -> consumer.accept(id));
        }
        confirmButton.addClassNames(buttonWidthClassName);

        Button cancelButton = new Button("Cancel", e -> currentUi.getUI().ifPresent(ui -> ui.navigate(navigateLink)));
        cancelButton.addClassNames("button--primary", buttonWidthClassName);

        return new HorizontalLayout(cancelButton, confirmButton);
    }

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

    public static void ErrorNotification() {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        notification.setDuration(3000);
        notification.add(new HorizontalLayout(new Div(new Text("Fill all the necessary fields!"))));
        notification.open();
    }
}
