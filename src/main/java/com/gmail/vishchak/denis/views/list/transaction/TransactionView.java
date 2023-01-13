package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Route(value = "", layout = MainLayout.class)
@PageTitle("Transactions | MoneyLonger")
@PermitAll
public class TransactionView extends VerticalLayout {
    private final static int ITEMS_PER_PAGE = 10;
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TransactionServiceImpl transactionService;
    private final Grid<Transaction> grid = new Grid<>(Transaction.class);
    private final TextField accountAmountFiled = new TextField();
    private final CurrentUser user;
    private final MenuBar menuBar = new MenuBar();
    private long totalAmountOfPages;
    private int currentPageNumber = 0;
    private Account currentAccount;
    private TransactionFilterForm form;


    public TransactionView(AccountServiceImpl accountService, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, TransactionServiceImpl transactionService, SecurityService securityService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.transactionService = transactionService;
        this.user = securityService.getAuthenticatedUser();

        addClassName("transaction-view");
        setSizeFull();

        configureMenuBar();
        configureAmountField();
        configureGrid();
        configureForm();

        HorizontalLayout toolBar = new HorizontalLayout(accountAmountFiled, menuBar);
        toolBar.setAlignItems(Alignment.BASELINE);

        add(
                toolBar,
                addContent(),
                getPageButtons()
        );

        updateList();

        if (user.getAccounts().isEmpty()) {
            UI.getCurrent().navigate(AccountCreateForm.class);
        }
    }

    private void configureMenuBar() {
        MenuItem accountMenu = menuBar.addItem("Account");
        SubMenu accountSubMenu = accountMenu.getSubMenu();
        accountSubMenu.addItem("Add account", e -> UI.getCurrent().navigate(AccountCreateForm.class));
        MenuItem chooseAccountMenu = accountSubMenu.addItem("Choose account");
        SubMenu accountVar = chooseAccountMenu.getSubMenu();

        accountVar.addItem("All accounts", e -> {
            currentAccount = null;
            updateList();
        });

        accountService.findAccountsByUser(user).forEach(account -> accountVar.addItem(account.getAccountName(), e -> {
            currentAccount = account;
            updateList();
        }));

        MenuItem transactionMenu = menuBar.addItem("Transaction");
        SubMenu transactionSubMenu = transactionMenu.getSubMenu();
        transactionSubMenu.addItem("Add transaction", e -> getUI().ifPresent(ui -> ui.navigate("add-transaction")));
        transactionSubMenu.addItem("Filter transactions", e -> form.setVisible(!form.isVisible()));

        ComponentEventListener<ClickEvent<MenuItem>> listener = e -> {
            if (grid.getColumns().size() > 5) {
                grid.removeColumnByKey("buttons");
                return;
            }
            showButtons();
        };

        menuBar.addItem("Edit", listener);
    }

    public void updateList() {
        ZoneId defaultZoneId = form.getDefaultZoneId();

        totalAmountOfPages = transactionService.getPageCount(user, currentAccount, ITEMS_PER_PAGE);

        if (form.isVisible()) {
            grid.setItems(
                    transactionService.findSpecificUserTransactions(user,
                            currentAccount,
                            form.getNoteField().getValue(),
                            form.getFromDateField().isEmpty() ? null : Date.from(form.getFromDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                            form.getToDateField().isEmpty() ? null : Date.from(form.getToDateField().getValue().atStartOfDay(defaultZoneId).toInstant()),
                            form.getAmountField().getValue(),
                            form.getCategory().isEmpty() ? null : form.getCategory().getValue(),
                            form.getSubcategory().isEmpty() ? null : form.getSubcategory().getValue(),
                            currentPageNumber, ITEMS_PER_PAGE)
            );
            return;
        }
        if (currentAccount == null) {
            grid.setItems(transactionService.findAllUSerTransactions(user, currentPageNumber, ITEMS_PER_PAGE));
        } else {
            grid.setItems(transactionService.findAllAccountTransactions(currentAccount, currentPageNumber, ITEMS_PER_PAGE));
        }

        updateAccountAmountField();
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

        SharedComponents.configureDialog(dialog, deleteButton);
    }

    private void configureForm() {
        form = new TransactionFilterForm(
                categoryService.findAllCategories(), subcategoryService.findAllSubcategories(), accountService);
        form.setWidth("25 em");

        form.getAmountField().addValueChangeListener(e -> updateList());
        form.getFromDateField().addValueChangeListener(e -> updateList());
        form.getToDateField().addValueChangeListener(e -> updateList());
        form.getNoteField().addValueChangeListener(e -> updateList());
        form.getCategory().addValueChangeListener(e -> updateList());
        form.getSubcategory().addValueChangeListener(e -> updateList());

        form.getClear().addClickListener(e -> form.clearForm());
        form.getClose().addClickListener(e -> form.setVisible(false));

        form.setVisible(false);
    }

    private Component addContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.addClassName("content");
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();

        return content;
    }

    private Component getPageButtons() {
        setClassName("page-buttons");

        Button nextButton = new Button("Next page", e -> {
            if (currentPageNumber >= --totalAmountOfPages) {
                return;
            }
            currentPageNumber++;
            updateList();
        });

        Button previousButton = new Button("Previous page", e -> {
            if (currentPageNumber <= 0) {
                return;
            }
            currentPageNumber--;
            updateList();
        });

        return new Div(previousButton, nextButton);
    }

    private void configureAmountField() {
        accountAmountFiled.setReadOnly(true);
        accountAmountFiled.setSizeUndefined();
        accountAmountFiled.setLabel("Account balance");
        accountAmountFiled.setPrefixComponent(VaadinIcon.DOLLAR.create());
    }

    private void updateAccountAmountField() {
        if (currentAccount != null) {
            accountAmountFiled.setValue(currentAccount.getAccountAmount().toString());
            return;
        }

        Double total = 0D;
        List<Account> accountList = accountService.findAccountsByUser(user);
        for (Account a :
                accountList) {
            total += a.getAccountAmount();
        }
        accountAmountFiled.setValue(total.toString());
    }
}