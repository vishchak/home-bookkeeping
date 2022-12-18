package com.gmail.vishchak.denis.views.list.sheared;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.ZoneId;

public class SharedComponents {
    public static DatePicker dateField(String format, String placeholder) {
        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat(format);

        DatePicker date = new DatePicker();
        date.setPlaceholder(placeholder);
        date.setI18n(singleFormatI18n);

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        date.setMax(now);

        return date;
    }

    public static NumberField amountField(String placeholder) {
        NumberField dollarField = new NumberField();
        dollarField.setPlaceholder(placeholder);
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        dollarField.setSizeFull();
        dollarField.setClearButtonVisible(true);

        return dollarField;
    }

    public static TextField textFiled(String label, String value) {
        TextField textField = new TextField();
        textField.setLabel(label);
        textField.setValue(value);
        textField.setClearButtonVisible(true);

        return textField;
    }
}
