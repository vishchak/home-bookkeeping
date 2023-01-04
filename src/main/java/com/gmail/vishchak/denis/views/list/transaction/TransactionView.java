package com.gmail.vishchak.denis.views.list.transaction;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.*;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Transactions | MoneyLonger")
@PermitAll
public class TransactionView extends VerticalLayout {
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TransactionServiceImpl transactionService;
    private final Grid<Transaction> grid = new Grid<>(Transaction.class);
    private final ComboBox<Account> accountComboBox = new ComboBox<>("Account");
    private final TextField accountAmountFiled = textFiled("");
    private final CurrentUser user;
    private long totalAmountOfPages;
    private int currentPageNumber = 0;
    private TransactionFilterForm form;

    public TransactionView(AccountServiceImpl accountService,
                           CategoryServiceImpl categoryService,
                           SubcategoryServiceImpl subcategoryService,
                           TransactionServiceImpl transactionService, CurrentUserServiceImpl userService, SecurityService securityService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.transactionService = transactionService;

        UserDetails userDetails = securityService.getAuthenticatedUser();
        this.user = userService.findUserByEmailOrLogin(userDetails.getUsername());

        addClassName("transaction-view");
        setSizeFull();

        configureAmountField();
        configureToolBar();
        configureGrid();
        configureForm();

        add(
                getToolbar(),
                addContent(),
                getPageButtons()
        );

        updateList();
    }

    private void configureToolBar() {
        accountComboBox.addValueChangeListener(e -> {
            updateAccountAmountField();
            updateList();
        });
    }

    public void updateList() {
        ZoneId defaultZoneId = form.getDefaultZoneId();
        //swap on current user
        List<Account> accountList = accountService.findAccountsByUser(user);

        if (!accountComboBox.isEmpty()) {
            int ITEMS_PER_PAGE = 10;
            totalAmountOfPages = transactionService.getPageCount(accountComboBox.getValue(), ITEMS_PER_PAGE);

            if (form.isVisible()) {
                grid.setItems(
                        transactionService.findAccountTransactions(accountComboBox.getValue(),
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
            grid.setItems(
                    transactionService.findAllTransactionByAccount(accountComboBox.getValue(),
                            currentPageNumber, ITEMS_PER_PAGE)
            );

        } else if (!accountList.isEmpty()) {
            accountComboBox.setValue(accountList.get(0));
        }
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

    private Component getToolbar() {

        VerticalLayout verticalLayout = new VerticalLayout(getAccountField(accountComboBox, accountService, user), accountAmountFiled);
        verticalLayout.setAlignItems(Alignment.AUTO);
        verticalLayout.setSizeUndefined();

        HorizontalLayout horizontalLayout = new HorizontalLayout(
                verticalLayout,
                getAddComponentButton("Add transaction", "add-transaction"),
                getFilterButton()
        );

        horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        horizontalLayout.setWidthFull();

        return horizontalLayout;
    }

    private Component getFilterButton() {
        Button filterButton = new Button("Filter transaction");
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        filterButton.setIcon(new Icon("lumo", "search"));

        filterButton.addClickListener(e -> form.setVisible(!form.isVisible()));

        return filterButton;
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
        accountAmountFiled.setValue(accountComboBox.getValue().getAccountAmount().toString());
    }
}