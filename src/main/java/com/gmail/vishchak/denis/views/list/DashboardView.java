package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.Transaction;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

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

        add(
                getNetIncomeChart(),
                createTabs()

        );
    }

    private Component getNetIncomeChart() {
        Chart bar = new Chart(ChartType.BAR);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Net income chart");

        ZoneId zoneId = ZoneId.systemDefault();
        Date start = Date.from(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(zoneId).toInstant());
        Date finish = Date.from(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(zoneId).toInstant());

        dataSeries.add(new DataSeriesItem(LocalDate.now().getMonth().name(), sumByCategory(start, finish)));
        bar.getConfiguration().setSeries(dataSeries);

        return bar;
    }

    private double sumByCategory(Date from, Date to) {
        double total = 0D;

        List<Transaction> transactionList = transactionService.findAccountTransactions(accountService.findByAccountId(1L).get(), null, from, to, null, null, null);
        for (Transaction t :
                transactionList) {
            if (t.getCategory().getCategoryName().equalsIgnoreCase("Expense")) {
                total -= t.getTransactionAmount();
            } else if (t.getCategory().getCategoryName().equalsIgnoreCase("Income")) {
                total += t.getTransactionAmount();
            } else if (t.getCategory().getCategoryName().equalsIgnoreCase("Other")) {
                if (t.getSubcategory().getSubcategoryName().equalsIgnoreCase("Debt collection") ||
                        t.getSubcategory().getSubcategoryName().equalsIgnoreCase("Loan")) {
                    total += t.getTransactionAmount();
                } else total -= t.getTransactionAmount();
            }
        }

        return total;
    }
//add by date
    private Component getChart(Long categoryId) {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Expenses by subcategory chart");

        transactionService.findByCategory(accountService.findByAccountId(1L).get(), categoryId)
                .forEach(transaction ->
                        dataSeries.add(
                                new DataSeriesItem(
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
