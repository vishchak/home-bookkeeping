package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;

import java.time.ZoneId;
import java.util.List;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.*;

@Getter
public class TransactionFilterForm extends FormLayout {
    private final NumberField amountField = amountField("Amount");
    private final TextField noteField = textFiled("Note");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final String format = "dd-MM-yyyy";

    private final ZoneId defaultZoneId = ZoneId.systemDefault();
    private final DatePicker fromDateField = dateField(format, "Start date");
    private final DatePicker toDateField = dateField(format, "Finish date");
    private final Button clear = new Button("clear");
    private final Button close = new Button("close");
    private final AccountServiceImpl accountService;

    public TransactionFilterForm(List<Category> categories, List<Subcategory> subcategories, AccountServiceImpl accountService) {
        this.accountService = accountService;

        addClassName("transaction-form");
        category.setItems(categories);
        category.setItemLabelGenerator(Category::getCategoryName);
        category.setSizeFull();

        subcategory.setItems(subcategories);
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);
        subcategory.setSizeFull();

        amountField.setValueChangeMode(ValueChangeMode.LAZY);
        noteField.setValueChangeMode(ValueChangeMode.LAZY);

        VerticalLayout formLayout = new VerticalLayout(
                new HorizontalLayout(fromDateField, toDateField),
                amountField,
                category,
                subcategory,
                noteField,
                new HorizontalLayout(clearFormButton(), closeFormButton())
        );

        formLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(formLayout);
    }

    private Component closeFormButton() {
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        return close;
    }

    private Component clearFormButton() {
        clear.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addClickShortcut(Key.ESCAPE);

        return clear;
    }

    protected void clearForm() {
        amountField.setValue(amountField.getEmptyValue());
        fromDateField.setValue(fromDateField.getEmptyValue());
        toDateField.setValue(toDateField.getEmptyValue());
        noteField.setValue(noteField.getEmptyValue());
        category.setValue(category.getEmptyValue());
        subcategory.setValue(subcategory.getEmptyValue());
    }
}
