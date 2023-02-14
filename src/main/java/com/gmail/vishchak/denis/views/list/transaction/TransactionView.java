package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.service.CategoryServiceImpl;
import com.gmail.vishchak.denis.service.SubcategoryServiceImpl;
import com.gmail.vishchak.denis.service.TransactionServiceImpl;
import com.gmail.vishchak.denis.views.list.account.AccountCreateForm;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Transactions | FROG-STOCK")
@CssImport("./themes/frog-stock/components/transaction-view/transaction-view.css")
public class TransactionView extends VerticalLayout {
    private final static int ITEMS_PER_PAGE = 10;
    private final AccountServiceImpl accountService;
    private final TransactionServiceImpl transactionService;
    private final Grid<Transaction> grid = new Grid<>(Transaction.class);
    private final MenuBar menuBar = new MenuBar();
    private final Button menuButton = new Button(new Icon("lumo", "unordered-list"));
    private final TransactionFilterForm form;
    private final CustomUser user;
    private long totalAmountOfPages;
    private int currentPageNumber = 0;
    private Account currentAccount;


    public TransactionView(AccountServiceImpl accountService, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, TransactionServiceImpl transactionService, SecurityService securityService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.user = securityService.getAuthenticatedUser();
        this.form = new TransactionFilterForm(categoryService.findAllCategories(), subcategoryService.findAllSubcategories(), this::updateList);

        addClassName("transaction-view");
        setSizeFull();

        configureMenuBar();
        configureGrid();

        menuButton.addClassNames("menu-button", "button--secondary");
        HorizontalLayout toolBar = new HorizontalLayout(menuBar, menuButton);
        toolBar.addClassName("transaction-view-toolbar");
        toolBar.setWidth(grid.getWidth());

        add(
                toolBar,
                addContent(),
                getPageButtons()
        );

        updateList();

        if (user.getAccounts().isEmpty()) {
            UI.getCurrent().getPage().open("add-account", "_self");
        }
    }

    private void configureMenuBar() {
        menuBar.removeAll();

        MenuItem accountMenu = menuBar.addItem("Account");
        SubMenu accountSubMenu = accountMenu.getSubMenu();
        accountSubMenu.addItem(accountAmount());
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

    private String accountAmount() {
        if (currentAccount != null) {
            return currentAccount.getAccountName() + ' ' + currentAccount.getAccountAmount().toString() + '$';
        }

        AtomicReference<Double> total = new AtomicReference<>(0D);
        accountService.findAccountsByUser(user).forEach(account -> total.updateAndGet(v -> v + account.getAccountAmount()));

        return "Total " + total + '$';
    }


    public void updateList() {
        ZoneId defaultZoneId = ZoneId.systemDefault();

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

        configureMenuBar();
    }

    private void configureGrid() {
        grid.addClassNames("transaction-grid", "gird-color");
        grid.setSizeFull();
        grid.setColumns();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        Grid.Column<Transaction> dateColumn = grid
                .addColumn(transaction -> transaction.getTransactionDate().toString()).setHeader("Date").setSortable(true);
        Grid.Column<Transaction> amountColumn = grid
                .addColumn(transaction -> transaction.getTransactionAmount().toString()).setHeader("Amount").setSortable(true);
        Grid.Column<Transaction> categoryColumn = grid
                .addColumn(transaction -> transaction.getCategory().getCategoryName()).setHeader("Category").setSortable(true);
        Grid.Column<Transaction> subcategoryColumn = grid
                .addColumn(transaction -> transaction.getSubcategory().getSubcategoryName()).setHeader("Subcategory");
        Grid.Column<Transaction> noteColumn = grid
                .addColumn(Transaction::getNote).setHeader("Note");

        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(menuButton);

        columnToggleContextMenu.addColumnToggleItem("Date", dateColumn);
        columnToggleContextMenu.addColumnToggleItem("Amount", amountColumn);
        columnToggleContextMenu.addColumnToggleItem("Category", categoryColumn);
        columnToggleContextMenu.addColumnToggleItem("Subcategory", subcategoryColumn);
        columnToggleContextMenu.addColumnToggleItem("Note", noteColumn);
    }

    private void showButtons() {
        grid.addComponentColumn(transaction -> {
                    Button editButton = new Button(new Icon("lumo", "edit"), e -> getUI().ifPresent(ui -> ui.navigate("add-transaction/" + transaction.getTransactionId())));
                    Button deleteButton = new Button(new Icon("vaadin", "trash"), e -> deleteTransaction(transaction));

                    editButton.addClassNames("button--tertiary");
                    deleteButton.addClassNames("button--primary");

                    return new HorizontalLayout(editButton, deleteButton);
                }).setKey("buttons")
                .setHeader("Edit");
    }

    private void deleteTransaction(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Are you sure you want to delete this transaction permanently?");

        Button deleteButton = new Button
                (
                        "Delete",
                        e -> {
                            transactionService.deleteTransaction(transaction.getTransactionId());
                            updateList();
                            dialog.close();
                        }
                );

        SharedComponents.configureDialog(dialog, deleteButton);
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
        Button nextButton = new Button(new Icon("lumo", "angle-right"), e -> {
            if (currentPageNumber >= --totalAmountOfPages) {
                return;
            }
            currentPageNumber++;
            updateList();
        });

        Button previousButton = new Button(new Icon("lumo", "angle-left"), e -> {
            if (currentPageNumber <= 0) {
                return;
            }
            currentPageNumber--;
            updateList();
        });

        return new Div(previousButton, nextButton);
    }

    private static class ColumnToggleContextMenu extends ContextMenu {
        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<Transaction> column) {
            MenuItem menuItem = this.addItem(label, e -> column.setVisible(e.getSource().isChecked()));
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }
}