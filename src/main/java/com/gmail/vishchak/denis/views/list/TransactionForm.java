package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;

import java.time.ZoneId;
import java.util.List;

import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.*;

@Getter
public class TransactionForm extends FormLayout {
   private final NumberField amountField = amountField("Amount");
    private final TextField noteField = textFiled("Note");
    private final  ComboBox<Category> category = new ComboBox<>("Category");
    private final ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");
    private final String format = "dd-MM-yyyy";
    private final  ZoneId defaultZoneId = ZoneId.systemDefault();
    private final DatePicker fromDateField = dateField(format, "Start date");
    private final  DatePicker toDateField = dateField(format, "Finish date");
    private final Button clear = new Button("clear");
    private final  Accordion filterField = new Accordion();

    public TransactionForm(List<Category> categories, List<Subcategory> subcategories) {
        addClassName("transaction-form");
        category.setItems(categories);
        category.setItemLabelGenerator(Category::getCategoryName);
        category.setSizeFull();

        subcategory.setItems(subcategories);
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);
        subcategory.setSizeFull();

        amountField.setValueChangeMode(ValueChangeMode.LAZY);
        noteField.setValueChangeMode(ValueChangeMode.LAZY);

        HorizontalLayout horizontalLayout = new HorizontalLayout
                (
                        getToolbar(),
                        filterField.add("Filter transaction", new VerticalLayout(
                                new HorizontalLayout(fromDateField, toDateField),
                                amountField,
                                category,
                                subcategory,
                                noteField,
                                clearFormButton()))
                );
        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(
                horizontalLayout
        );
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

    private Component getToolbar() {
        Button addTransactionButton = new Button("Add transaction");
        addTransactionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTransactionButton.setIcon(new Icon("lumo", "plus"));

        addTransactionButton.addClickListener(e -> addTransactionButton.getUI().ifPresent(ui ->
                ui.navigate("add-transaction")));

        return addTransactionButton;
    }
}
