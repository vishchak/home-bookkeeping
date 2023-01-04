package com.gmail.vishchak.denis.views.list.shared;

import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.views.list.goal.GoalView;
import com.gmail.vishchak.denis.views.list.chart.DashboardView;
import com.gmail.vishchak.denis.views.list.transaction.TransactionView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;


@Getter
public class MainLayout extends AppLayout {
    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("MoneyLonger");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Log out", e -> securityService.logout());
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);

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
