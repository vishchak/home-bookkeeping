package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.dateField;

@Getter
public class TransactionFilterForm extends FormLayout {
    private final NumberField amountField = new NumberField("Amount");
    private final TextField noteField = new TextField("Note");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final DatePicker fromDateField = dateField("dd-MM-yyyy", "Start date");
    private final DatePicker toDateField = dateField("dd-MM-yyyy", "Finish date");

    public TransactionFilterForm(List<Category> categories, List<Subcategory> subcategories, Runnable run) {
        this.setVisible(false);

        addClassName("transaction-form");
        category.setItems(categories);
        category.setItemLabelGenerator(Category::getCategoryName);

        subcategory.setItems(subcategories);
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);

        amountField.setValueChangeMode(ValueChangeMode.LAZY);
        noteField.setValueChangeMode(ValueChangeMode.LAZY);

        Stream.of(new AbstractSinglePropertyField[]{amountField, toDateField, fromDateField, noteField, category, subcategory}).forEach(f -> f.addValueChangeListener(e -> run.run()));

        VerticalLayout formLayout = new VerticalLayout(
                new Div(fromDateField, toDateField),
                amountField,
                category,
                subcategory,
                noteField,
                new Div
                        (
                                new Button("clear", e -> Stream.of(new AbstractSinglePropertyField[]{amountField, toDateField, fromDateField, noteField, category, subcategory}).forEach(f -> f.setValue(f.getEmptyValue()))),
                                new Button("close", e -> this.setVisible(false))
                        )
        );
        formLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(formLayout);
    }
}
