package com.gmail.vishchak.denis.views.list.chart;

import com.gmail.vishchak.denis.model.Category;
import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Transaction;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.PermitAll;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.dateField;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | MoneyLonger")
@PermitAll
public class DashboardView extends VerticalLayout {
    private final CategoryServiceImpl categoryService;
    private final TransactionServiceImpl transactionService;
    private final Tab expense = new Tab("Expense chart");
    private final Tab income = new Tab("Income chart");
    private final Tab other = new Tab("Other chart");
    private final Tab netIncome = new Tab("Net income");
    private final VerticalLayout content = new VerticalLayout();
    private final String format = "dd-MM-yyyy";
    private final ZoneId defaultZoneId = ZoneId.systemDefault();
    private final DatePicker fromDateField = dateField(format, "Start date");
    private final DatePicker toDateField = dateField(format, "Finish date");
    private final CurrentUser user;

    public DashboardView(CurrentUserServiceImpl userService,
                         CategoryServiceImpl categoryService,
                         TransactionServiceImpl transactionService, SecurityService securityService) {
        this.categoryService = categoryService;
        this.transactionService = transactionService;

        UserDetails userDetails = securityService.getAuthenticatedUser();
        this.user = userService.findUserByEmailOrLogin(userDetails.getUsername());


        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        fromDateField.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        updateConfig(fromDateField, toDateField);


        toDateField.setValue(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
        toDateField.setMin(fromDateField.getValue());
        updateConfig(toDateField, fromDateField);

        add(
                new HorizontalLayout(fromDateField, toDateField),
                createTabs()

        );
    }

    private void updateConfig(DatePicker datePickerFrom, DatePicker datePickerTo) {
        datePickerFrom.addValueChangeListener(e -> {
            if (!datePickerFrom.isEmpty() && !datePickerTo.isEmpty()) {
                updateList();
            }
        });
    }

    private void updateList() {
        removeAll();

        add(new HorizontalLayout(fromDateField, toDateField),
                getNetIncomeChart(),
                createTabs());
    }

    private Component getNetIncomeChart() {
        Chart bar = new Chart(ChartType.BAR);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Net income chart");

        Date from = Date.from(fromDateField.getValue().atStartOfDay(defaultZoneId).toInstant());
        Date to = Date.from(toDateField.getValue().atStartOfDay(defaultZoneId).toInstant());

        dataSeries.add(new DataSeriesItem(LocalDate.now().getMonth().name(), sumByCategory(from, to)));
        bar.getConfiguration().setSeries(dataSeries);

        return bar;
    }

    private double sumByCategory(Date from, Date to) {
        double total = 0D;

        List<Transaction> transactionList = transactionService.findChartTransactions(user, from, to, null);
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

    private Component getChart(Long categoryId) {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        dataSeries.setName("Expenses by subcategory chart");

        Date from = Date.from(fromDateField.getValue().atStartOfDay(defaultZoneId).toInstant());
        Date to = Date.from(toDateField.getValue().atStartOfDay(defaultZoneId).toInstant());

        Map<String, Double> subcategoryAmount = new HashMap<>();

        Optional<Category> category = categoryService.findCategoryById(categoryId);

        category.ifPresent(c -> transactionService.findChartTransactions(user, from, to, c)
                .forEach(transaction -> {
                    String key = transaction.getSubcategory().getSubcategoryName();
                    if (subcategoryAmount.containsKey(key)) {
                        subcategoryAmount.replace(key, (subcategoryAmount.get(transaction.getSubcategory().getSubcategoryName()) + transaction.getTransactionAmount()));
                    } else {
                        subcategoryAmount.put(key,
                                transaction.getTransactionAmount());
                    }
                }));

        for (Map.Entry<String, Double> pair : subcategoryAmount.entrySet()) {
            dataSeries.add(new DataSeriesItem(pair.getKey() + System.lineSeparator() + pair.getValue(), pair.getValue()));
        }

        chart.getConfiguration().setSeries(dataSeries);

        return chart;
    }

    private Component createTabs() {
        Tabs tabs = new Tabs();
        tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));
        tabs.add(netIncome, income, expense, other);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.setSelectedTab(netIncome);

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
        } else if (tab.equals(netIncome)) {
            content.add(new Paragraph(getNetIncomeChart()));
        }
    }
}
