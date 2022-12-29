package com.gmail.vishchak.denis.views.list.shared;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.views.list.goal.GoalView;
import com.gmail.vishchak.denis.views.list.chart.DashboardView;
import com.gmail.vishchak.denis.views.list.transaction.TransactionView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;

import java.util.List;

@Getter
public class MainLayout extends AppLayout {

    private final ComboBox<Account> accountComboBox = new ComboBox<>("Account");
    private final AccountServiceImpl accountService;

    public MainLayout(AccountServiceImpl accountService) {
        this.accountService = accountService;

        createHeader();
        createAccountBox();
        createDrawer();
    }

    private void createAccountBox() {
        //replace with currentUser id after security
        List<Account> accounts = accountService.findAccountsByUserId(1L);

        accountComboBox.setItems(accounts);
        accountComboBox.setItemLabelGenerator(Account::getAccountName);
        accountComboBox.setSizeFull();
        accountComboBox.setHelperText("Choose account");

        if(accountComboBox.isEmpty()){
            accountComboBox.setValue(accounts.get(0));
        }

        addToDrawer(new VerticalLayout(
                accountComboBox
        ));
    }

    private void createHeader() {
        H1 logo = new H1("MoneyLonger");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink transactionList = new RouterLink("Transaction list", TransactionView.class);
        transactionList.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink dashboard = new RouterLink("Dashboards", DashboardView.class);

        RouterLink goals = new RouterLink("Goals", GoalView.class);
        addToDrawer(new VerticalLayout(
                transactionList,
                dashboard,
                goals
        ));
    }
}
