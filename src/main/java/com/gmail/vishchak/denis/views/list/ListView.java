package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.views.list.sheared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;


@Route(value = "")
@PageTitle("Transactions list")
public class ListView extends VerticalLayout {
    Grid<Transaction> grid = new Grid<>(Transaction.class);
    TransactionForm form;

    public ListView() {
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(
                getToolbar(),
                addContent()
        );
    }

    private void configureGrid() {
        grid.addClassNames("transaction-grid");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(transaction -> transaction.getTransactionAmount().toString()).setHeader("Amount");
        grid.addColumn("note");
        grid.addColumn(transaction -> transaction.getTransactionDate().toString()).setHeader("Date");
        grid.addColumn(transaction -> transaction.getCategory().getCategoryName()).setHeader("Category");
        grid.addColumn(transaction -> transaction.getSubcategory().getSubcategoryName()).setHeader("Subcategory");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void configureForm() {
        form = new TransactionForm(Collections.emptyList(), Collections.emptyList());
        form.setWidth("25 em");
    }


    private HorizontalLayout getToolbar() {
        Button addTransactionButton = new Button("Add transaction");

        NumberField amountField = SharedComponents.amountField("Filter by amount");
        amountField.setValueChangeMode(ValueChangeMode.LAZY);

        HorizontalLayout toolbar = new HorizontalLayout(addTransactionButton,
                amountField,
                SharedComponents.dateField("dd-MM-yyyy", "Filter by date:"));

        toolbar.addClassName("toolbar");
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        return toolbar;
    }

    private Component addContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }
}