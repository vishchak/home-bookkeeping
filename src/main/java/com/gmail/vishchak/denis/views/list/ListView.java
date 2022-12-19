package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static com.gmail.vishchak.denis.views.list.sheared.SharedComponents.*;

@Route(value = "")
@PageTitle("Transactions list")
public class ListView extends VerticalLayout {
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final CurrentUserServiceImpl currentUserService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TransactionServiceImpl transactionService;
    Grid<Transaction> grid = new Grid<>(Transaction.class);
    TransactionForm form;
    NumberField amountField;
    TextField textField;
    String format = "dd-MM-yyyy";
    ZoneId defaultZoneId = ZoneId.systemDefault();
    LocalDate localDate = LocalDate.now();
    DatePicker fromDateField = dateField(format, "Start date", LocalDate.of(1970, 1, 1));
    DatePicker toDateField = dateField(format, "Finish date", localDate);

    public ListView(AccountServiceImpl accountService,
                    CategoryServiceImpl categoryService,
                    CurrentUserServiceImpl currentUserService,
                    SubcategoryServiceImpl subcategoryService,
                    TransactionServiceImpl transactionService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
        this.subcategoryService = subcategoryService;
        this.transactionService = transactionService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(
                getToolbar(),
                addContent()
        );

        updateList();
    }

    private void updateList() {
        Account currentAccount = accountService.findByAccountName(
                "test account", currentUserService.findUserByEmailOrLogin(
                        "test user"));

        if (fromDateField.isEmpty() || toDateField.isEmpty()) {
            grid.setItems(transactionService.findAccountTransactions(currentAccount,
                    textField.getValue(),
                    null,
                    null));
            return;
        }

        grid.setItems(transactionService.findAccountTransactions(currentAccount,
                textField.getValue(),
                Date.from(fromDateField.getValue().atStartOfDay(defaultZoneId).toInstant()),
                Date.from(toDateField.getValue().atStartOfDay(defaultZoneId).toInstant())));
    }

    private void configureGrid() {
        grid.addClassNames("transaction-grid");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(transaction -> transaction.getTransactionDate().toString()).setHeader("Date");
        grid.addColumn(transaction -> transaction.getTransactionAmount().toString()).setHeader("Amount");
        grid.addColumn(transaction -> transaction.getCategory().getCategoryName()).setHeader("Category");
        grid.addColumn(transaction -> transaction.getSubcategory().getSubcategoryName()).setHeader("Subcategory");
        grid.addColumn("note");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void configureForm() {
        form = new TransactionForm(categoryService.findAllCategories(), subcategoryService.findAllCategories());
        form.setWidth("25 em");
    }


    private HorizontalLayout getToolbar() {
        Button addTransactionButton = new Button("Add transaction");

        amountField = amountField("Filter by amount");
        amountField.setValueChangeMode(ValueChangeMode.LAZY);

        textField = textFiled("Filter by note");
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.addValueChangeListener(e -> updateList());

        fromDateField.addValueChangeListener(e -> updateList());
        toDateField.addValueChangeListener(e -> updateList());

        HorizontalLayout toolbar = new HorizontalLayout(addTransactionButton,
                amountField,
                fromDateField,
                toDateField,
                textField);

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