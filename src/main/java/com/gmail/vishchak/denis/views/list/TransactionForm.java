package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.Subcategory;
import com.gmail.vishchak.denis.views.list.sheared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class TransactionForm extends FormLayout {
    NumberField transactionAmount = SharedComponents.amountField("Amount");
    ComboBox<Category> category = new ComboBox<>("Category");
    ComboBox<Subcategory> subcategory = new ComboBox<>("Subcategory");

    Button add = new Button("add");
    Button delete = new Button("delete");
    Button cancel = new Button("cancel");

    public TransactionForm(List<Category> categories, List<Subcategory> subcategories) {
        addClassName("transaction-form");

        category.setItems(categories);
        category.setItemLabelGenerator(Category::getCategoryName);

        subcategory.setItems(subcategories);
        subcategory.setItemLabelGenerator(Subcategory::getSubcategoryName);

        add(
                transactionAmount,
                category,
                subcategory,
                createButtonsLayout()
        );
    }

    private Component createButtonsLayout() {
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add.addClickShortcut(Key.ENTER);
        delete.addClickShortcut(Key.DELETE);
        cancel.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(add, delete, cancel);
    }
}
