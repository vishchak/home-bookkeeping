package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | MoneyLonger")
public class DashboardView extends VerticalLayout {
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final CurrentUserServiceImpl currentUserService;
    private final SubcategoryServiceImpl subcategoryService;
    private final TransactionServiceImpl transactionService;
    private final Tab expense = new Tab("Expense chart");
    private final Tab income = new Tab("Income chart");
    private final Tab other = new Tab("Other chart");
    private final VerticalLayout content = new VerticalLayout();

    public DashboardView(AccountServiceImpl accountService,
                         CategoryServiceImpl categoryService,
                         CurrentUserServiceImpl currentUserService,
                         SubcategoryServiceImpl subcategoryService,
                         TransactionServiceImpl transactionService) {
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
        this.subcategoryService = subcategoryService;
        this.transactionService = transactionService;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(createTabs());
    }

    private Component getNetIncomeChart() {


        return null;
    }

    private Component getChart(Long categoryId) {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Expenses by subcategory chart");

        transactionService.findByCategory(accountService.findByAccountId(1L).get(), categoryId)
                .forEach(transaction -> dataSeries.add(new DataSeriesItem(
                        transaction.getSubcategory().getSubcategoryName(),
                        transaction.getTransactionAmount())));

        chart.getConfiguration().setSeries(dataSeries);

        return chart;
    }

    private Component createTabs() {
        Tabs tabs = new Tabs();
        tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));
        tabs.add(income, expense, other);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.setSelectedTab(expense);

        content.setSpacing(false);

        return new Div(tabs, content);
    }

    private void setContent(Tab tab) {
        content.removeAll();
        if (tab == null) {
            return;
        }
        if (tab.equals(expense)) {
            content.add(new Paragraph((getChart(1L))));
        } else if (tab.equals(income)) {
            content.add(new Paragraph((getChart(2L))));
        } else if (tab.equals(other)) {
            content.add(new Paragraph((getChart(3L))));
        }
    }
}
