package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        add(getExpensesChart());
    }

    private Component getNetIncomeChart() {


        return null;
    }

    private Component getExpensesChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Expenses by subcategory chart");

        transactionService.findByCategory(accountService.findByAccountId(1L).get(), 1L)
                .forEach(transaction -> {
                    dataSeries.add(new DataSeriesItem(transaction.getSubcategory().getSubcategoryName(), transaction.getTransactionAmount()));
                });

        chart.getConfiguration().setSeries(dataSeries);

        return chart;
    }
}
