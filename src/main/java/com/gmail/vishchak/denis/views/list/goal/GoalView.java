package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.service.GoalServiceImpl;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@PermitAll
@PageTitle("Goals | FROG-STOCK")
@Route(value = "goals", layout = MainLayout.class)
@CssImport("./themes/flowcrmtutorial/components/goal/goal-view.css")
public class GoalView extends VerticalLayout {
    private final static int ITEMS_PER_PAGE = 5;
    private final GoalServiceImpl goalService;
    private final AccountServiceImpl accountService;
    private final Grid<Goal> grid = new Grid<>(Goal.class);
    private final TextField filterField = new TextField("Search", e -> updateList());
    private final CheckboxGroup<GoalProgress> checkboxGroup = new CheckboxGroup<>("Progress", e -> updateList(), GoalProgress.values());
    private final FormLayout filterForm = new FormLayout(new VerticalLayout(filterField, checkboxGroup));
    private final MenuBar menuBar = new MenuBar();
    private final CustomUser user;
    private long totalAmountOfPages;
    private int currentPageNumber = 0;

    public GoalView(GoalServiceImpl goalService, AccountServiceImpl accountService, SecurityService securityService) {

        this.goalService = goalService;
        this.accountService = accountService;
        this.user = securityService.getAuthenticatedUser();

        addClassName("goals-view");
        setSizeFull();
        setAlignItems(Alignment.BASELINE);

        configureMenuBar();
        configureFields();
        configureGrid();

        add
                (
                        menuBar,
                        addContent(),
                        getPageButtons()
                );

        updateList();
    }

    private Component addContent() {
        HorizontalLayout content = new HorizontalLayout(grid, filterForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, filterForm);
        content.setSizeFull();

        return content;
    }

    private void configureMenuBar() {
        filterForm.setVisible(false);

        MenuItem addMenu = menuBar.addItem("Goal");
        addMenu.getSubMenu().addItem("Add goal", e -> getUI().ifPresent(ui -> ui.navigate("add-goal")));
        addMenu.getSubMenu().addItem("Add funds",
                e -> {
                    Grid.Column<Goal> column = grid.getColumnByKey("add");
                    column.setVisible(!column.isVisible());
                });

        menuBar.addItem("Filter", e -> filterForm.setVisible(!filterForm.isVisible()));
        menuBar.addItem("Edit", e -> edit());
    }

    private void configureFields() {
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        filterField.setPrefixComponent(new Icon("lumo", "search"));
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.setClearButtonVisible(true);
    }

    private void updateList() {
        totalAmountOfPages = goalService.getPageCount(user, ITEMS_PER_PAGE);

        List<Goal> goalList = goalService.findUserGoals(user.getUserId(), filterField.isEmpty() ? null : filterField.getValue(), checkboxGroup.isEmpty() ? Set.of(GoalProgress.values()) : checkboxGroup.getSelectedItems(),
                currentPageNumber, ITEMS_PER_PAGE);
        grid.setItems(goalList.isEmpty() ? Collections.emptyList() : goalList);
    }

    private void configureGrid() {
        grid.addClassNames("goals-grid", "gird-color");
        grid.setSizeFull();
        grid.setClassNameGenerator(goal -> {
            if (goal.getGoalProgress().equals(GoalProgress.CURRENT)) {
                return "current";
            }
            if (goal.getGoalProgress().equals(GoalProgress.FAILED)) {
                return "failed";
            }
            return "completed";
        });

        grid.setColumns();
        grid.addComponentColumn(goal -> new Button(new Icon("lumo", "plus"), e -> createAddFundsDialog(goal.getGoalId()))).setHeader("Add funds").setKey("add").setVisible(false);
        grid.addComponentColumn(this::progressBar).setHeader("Progress");
        grid.setItemDetailsRenderer(createPersonDetailsRenderer());
    }


    private Component progressBar(Goal goal) {
        ProgressBar progressBar = new ProgressBar(0, goal.getGoalAmount());

        String label = goal.getGoalNote() + " (" + goal.getCurrentAmount() + "/" + goal.getGoalAmount() + ")";

        return new VerticalLayout(new Div(new Text(label)), progressBar);
    }

    private void createAddFundsDialog(Long goalId) {
        ComboBox<Account> accountComboBox = new ComboBox<>("Withdraw from", accountService.findAccountsByUser(user));
        NumberField addAmount = new NumberField("Amount to be added");
        Dialog addFundsDialog = new Dialog(new VerticalLayout(addAmount, accountComboBox));

        goalService.findById(goalId).ifPresent
                (
                        g -> {
                            accountComboBox.setItemLabelGenerator(Account::getAccountName);
                            accountComboBox.setRequired(true);

                            addFundsDialog.getFooter().add(createDialogButtons(g.getGoalId(), addFundsDialog, accountComboBox, addAmount.getValue()));
                            addFundsDialog.setHeaderTitle(g.getGoalNote());
                            addFundsDialog.open();
                        }
                );
    }

    private void edit() {
        String columnKey = "edit";
        Grid.Column<Goal> edit = grid.getColumnByKey(columnKey);

        if (edit == null) {
            grid.addComponentColumn(goal -> {
                Button editButton = new Button(new Icon("lumo", "edit"), e -> getUI().ifPresent(ui -> ui.navigate("add-goal/" + goal.getGoalId())));
                Button deleteButton = new Button(new Icon("vaadin", "trash"), e -> deleteGoal(goal));

                return new HorizontalLayout(editButton, deleteButton);
            }).setKey(columnKey).setHeader("Edit");
            return;
        }
        grid.removeColumnByKey(columnKey);
    }

    private void deleteGoal(Goal goal) {
        Dialog deleteGoalDialog = new Dialog(new H2("Are you sure you want to delete this goal permanently?"));

        Button deleteButton = new Button("Delete",
                e -> {
                    goalService.deleteGoal(goal.getGoalId());
                    updateList();
                    deleteGoalDialog.close();
                });

        deleteGoalDialog.getFooter().add
                (
                        new Button("Cancel", e -> deleteGoalDialog.close()),
                        deleteButton
                );

        deleteGoalDialog.open();
    }

    private Component createDialogButtons(Long goalId, Dialog dialog, ComboBox<Account> accountComboBox, Double amount) {
        String buttonWidth = "goal-view-add-funds-dialog-button-width";

        Button confirm = new Button("Confirm",
                e -> {
                    goalService.addMoney(goalId, amount, accountComboBox.getValue());
                    dialog.close();
                    updateList();
                }
        );
        confirm.addClassName(buttonWidth);

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addClassNames("button--primary", buttonWidth);

        return new HorizontalLayout(cancel, confirm);
    }

    private Component getPageButtons() {
        setClassName("page-buttons-goal");

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

    private static String daysLeft(Goal goal) {
        switch (goal.getGoalProgress()) {
            case COMPLETED:
                return "Success";
            case CURRENT:
                long timeLeft = goal.getFinishDate().getTime() - (new Date().getTime());
                return String.valueOf((timeLeft / (1000 * 60 * 60 * 24) + 1));
            default:
                return "Failed";
        }
    }

    private static ComponentRenderer<GoalDetailsFormLayout, Goal> createPersonDetailsRenderer() {
        return new ComponentRenderer<>(GoalDetailsFormLayout::new,
                GoalDetailsFormLayout::setGoal);
    }

    private static class GoalDetailsFormLayout extends FormLayout {
        private final TextField startDateField = new TextField("Started on");
        private final TextField finishDate = new TextField("Finishes on");
        private final TextField daysLeft = new TextField("Days left");

        public GoalDetailsFormLayout() {
            Stream.of(startDateField, finishDate, daysLeft).forEach(field -> {
                field.setReadOnly(true);
                add(field);
            });
            setResponsiveSteps(new ResponsiveStep("0", 3));
        }

        public void setGoal(Goal goal) {
            startDateField.setValue(goal.getStartDate().toString());
            finishDate.setValue(goal.getFinishDate().toString());
            daysLeft.setValue(daysLeft(goal));
        }
    }
}