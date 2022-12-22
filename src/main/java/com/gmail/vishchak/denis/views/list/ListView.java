package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
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
                addContent()
        );

        updateList();
    }

    private void updateList() {
        ZoneId defaultZoneId = form.defaultZoneId;
        Account currentAccount = accountService.findByAccountName(
                "test account", currentUserService.findUserByEmailOrLogin(
                        "test user"));

        grid.setItems(
                transactionService.findAccountTransactions(currentAccount,
                        form.noteField.getValue(),
                        form.fromDateField.isEmpty() ? null : Date.from(form.fromDateField.getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.toDateField.isEmpty() ? null : Date.from(form.toDateField.getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.amountField.getValue(),
                        form.category.isEmpty() ? null : form.category.getValue().getCategoryName(),
                        form.subcategory.isEmpty() ? null : form.subcategory.getValue().getSubcategoryName())
        );
    }

    private void configureGrid() {
        grid.addClassNames("transaction-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setColumns();
        grid.addColumn(transaction -> transaction.getTransactionDate().toString()).setHeader("Date").setSortable(true);
        grid.addColumn(transaction -> transaction.getTransactionAmount().toString()).setHeader("Amount").setSortable(true);
        grid.addColumn(transaction -> transaction.getCategory().getCategoryName()).setHeader("Category").setSortable(true);
        grid.addColumn(transaction -> transaction.getSubcategory().getSubcategoryName()).setHeader("Subcategory").setSortable(true);
        grid.addColumn("note");
        grid.addComponentColumn(transaction -> {
            Button editButton = new Button("Edit");
            editButton.setIcon(new Icon("lumo", "edit"));
            editButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("add-transaction/" + transaction.getTransactionId())));
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> deleteTransaction(transaction));
            return new HorizontalLayout(editButton, deleteButton);
        });
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

    }

    private void deleteTransaction(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Are you sure you want to delete this transaction permanently?");

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener((e) -> {
            transactionService.deleteTransaction(transaction.getTransactionId());
            updateList();
            dialog.close();
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);

        dialog.open();
    }


    private void configureForm() {
        form = new TransactionForm(
                categoryService.findAllCategories(), subcategoryService.findAllCategories());
        form.setWidth("25 em");

        form.amountField.addValueChangeListener(e -> updateList());
        form.fromDateField.addValueChangeListener(e -> updateList());
        form.toDateField.addValueChangeListener(e -> updateList());
        form.noteField.addValueChangeListener(e -> updateList());
        form.category.addValueChangeListener(e -> updateList());
        form.subcategory.addValueChangeListener(e -> updateList());

        form.clear.addClickListener(e -> form.clearForm());
    }



    private Component addContent() {
        HorizontalLayout content = new HorizontalLayout(form, grid);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }
}