package com.gmail.vishchak.denis.views.list.shared;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.service.GoalServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
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

    public static void checkGoalUser(Long goalId, GoalServiceImpl goalService, CustomUser user) {
        goalService.findById(goalId).ifPresent(g -> {
            if (!Objects.equals(user.getUserId(), g.getUser().getUserId())) {
                Notification.show("Access denied!", 3000, Notification.Position.BOTTOM_START);
                UI.getCurrent().getPage().open("goals", "_self");
            }
        });
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

    public static TextField textFiled(String value) {
        TextField textField = new TextField();
        textField.setLabel(value);
        textField.setClearButtonVisible(true);
        textField.setSizeFull();

        return textField;
    }

    public static void ErrorNotification() {
        Notification.show("Fill all the necessary fields", 3000, Notification.Position.BOTTOM_START);
    }
}
