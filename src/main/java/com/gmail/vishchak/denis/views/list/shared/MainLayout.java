package com.gmail.vishchak.denis.views.list.shared;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.views.list.chart.DashboardView;
import com.gmail.vishchak.denis.views.list.goal.GoalView;
import com.gmail.vishchak.denis.views.list.transaction.TransactionView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;


public class MainLayout extends AppLayout {
    private static final String GIT_URL = "https://github.com/vishchak/home-bookkeeping#readme";
    private final SecurityService securityService;
    private final CurrentUser user;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        this.user = securityService.getAuthenticatedUser();

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("FROG-STOCK");
        logo.addClassNames("text-l", "m-m");


        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, getAvatarMenuBar());

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        addClassNames("main-layout-drawer");

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

    private Component getAvatarMenuBar() {
        Avatar avatar = new Avatar(user.getLogin());
        avatar.setImage(user.getPictureUrl());
        avatar.addClassName("user-avatar");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        menuBar.addClassName("user-menu-bar");

        Button logout = new Button("Log out", e -> securityService.logout());
        logout.addClassName("button");

        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem(user.getLogin() == null ? user.getEmail() : user.getLogin());
        subMenu.addItem(new Anchor(GIT_URL, "About"));
        subMenu.addItem(logout);

        return menuBar;
    }
}
