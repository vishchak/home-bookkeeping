package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.ZoneId;
import java.util.Date;


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
        ZoneId defaultZoneId = form.getDefaultZoneId();
        Account currentAccount = accountService.findByAccountName(
                "test account", currentUserService.findUserByEmailOrLogin(
                        "test user"));

        grid.setItems(
                transactionService.findAccountTransactions(currentAccount,
                        form.getNoteField().getValue(),
                        form.getFromDateField().isEmpty() ? null : Date.from(form.getFromDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.getToDateField().isEmpty() ? null : Date.from(form.getToDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.getAmountField().getValue(),
                        form.getCategory().isEmpty() ? null : form.getCategory().getValue().getCategoryName(),
                        form.getSubcategory().isEmpty() ? null : form.getSubcategory().getValue().getSubcategoryName())
        );
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
        form = new TransactionForm(
                categoryService.findAllCategories(), subcategoryService.findAllCategories());
        form.setWidth("25 em");

        form.getAmountField().addValueChangeListener(e -> updateList());
        form.getFromDateField().addValueChangeListener(e -> updateList());
        form.getToDateField().addValueChangeListener(e -> updateList());
        form.getNoteField().addValueChangeListener(e -> updateList());
        form.getCategory().addValueChangeListener(e -> updateList());
        form.getSubcategory().addValueChangeListener(e -> updateList());
    }

    private HorizontalLayout getToolbar() {
        Button addTransactionButton = new Button("Add");

        addTransactionButton.addClickListener(e -> addTransactionButton.getUI().ifPresent(ui ->
                ui.navigate("add-transaction")));

        HorizontalLayout toolbar = new HorizontalLayout(addTransactionButton);

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