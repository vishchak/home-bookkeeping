package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.*;

@Getter
public class TransactionForm extends FormLayout {
    private final NumberField amountField = amountField("Amount");
    private final TextField noteField = textFiled("Note");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final String format = "dd-MM-yyyy";

    private final ZoneId defaultZoneId = ZoneId.systemDefault();
    private final LocalDate localDate = LocalDate.now();
    private final DatePicker fromDateField = dateField(format, "Start date", LocalDate.of(1970, 1, 1));
    private final DatePicker toDateField = dateField(format, "Finish date", localDate);
    private final Button cancel = new Button("cancel");

    public TransactionForm(List<Category> categories, List<Subcategory> subcategories) {
        addClassName("transaction-form");

        category.setItems(categories);
        category.setItemLabelGenerator(Category::getCategoryName);

        subcategory.setItems(subcategories);
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);

        amountField.setValueChangeMode(ValueChangeMode.LAZY);
        noteField.setValueChangeMode(ValueChangeMode.LAZY);

        add(
                amountField,
                new HorizontalLayout(fromDateField,
                        toDateField),
                category,
                subcategory,
                noteField,
                createButtonsLayout()
        );
    }

    private Component createButtonsLayout() {
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancel.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(cancel);
    }
}
