package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
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
import java.util.Optional;


@Route(value = "", layout = MainLayout.class)
@PageTitle("Transactions | MoneyLonger")
public class TransactionView extends VerticalLayout {
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final CurrentUserServiceImpl currentUserService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TransactionServiceImpl transactionService;
    Grid<Transaction> grid = new Grid<>(Transaction.class);
    TransactionFilterForm form;

    public TransactionView(AccountServiceImpl accountService,
                           CategoryServiceImpl categoryService,
                           CurrentUserServiceImpl currentUserService,
                           SubcategoryServiceImpl subcategoryService,
                           TransactionServiceImpl transactionService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
        this.subcategoryService = subcategoryService;
        this.transactionService = transactionService;

        addClassName("transaction-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(
                addContent()
        );

        updateList();
    }

    private void updateList() {
        ZoneId defaultZoneId = form.getDefaultZoneId();
        Optional<Account> currentAccount = accountService.findByAccountId(1L);

        currentAccount.ifPresent(account -> grid.setItems(
                transactionService.findAccountTransactions(account,
                        form.getNoteField().getValue(),
                        form.getFromDateField().isEmpty() ? null : Date.from(form.getFromDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.getToDateField().isEmpty() ? null : Date.from(form.getToDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                        form.getAmountField().getValue(),
                        form.getCategory().isEmpty() ? null : form.getCategory().getValue().getCategoryName(),
                        form.getSubcategory().isEmpty() ? null : form.getSubcategory().getValue().getSubcategoryName())
        ));
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

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (grid.getColumns().size() > 5) {
                grid.removeColumnByKey("buttons");
                return;
            }
            showButtons();
        });
    }


    private void showButtons() {
        grid.addComponentColumn(transaction -> {
            Button editButton = new Button("Edit");
            editButton.setIcon(new Icon("lumo", "edit"));
            editButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("add-transaction/" + transaction.getTransactionId())));

            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> deleteTransaction(transaction));

            return new HorizontalLayout(editButton, deleteButton);
        }).setKey("buttons").setHeader("Edit");
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

        SharedComponents.configureDialog(dialog,deleteButton);
    }


    private void configureForm() {
        form = new TransactionFilterForm(
                categoryService.findAllCategories(), subcategoryService.findAllSubcategories());
        form.setWidth("25 em");

        form.getAmountField().addValueChangeListener(e -> updateList());
        form.getFromDateField().addValueChangeListener(e -> updateList());
        form.getToDateField().addValueChangeListener(e -> updateList());
        form.getNoteField().addValueChangeListener(e -> updateList());
        form.getCategory().addValueChangeListener(e -> updateList());
        form.getSubcategory().addValueChangeListener(e -> updateList());

        form.getClear().addClickListener(e -> form.clearForm());
    }


    private Component addContent() {
        VerticalLayout content = new VerticalLayout(form, grid);
        content.addClassName("content");
        content.setSizeFull();

        return content;
    }
}