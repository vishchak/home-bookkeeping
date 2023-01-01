package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.MainLayout;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Date;
import java.util.Set;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.textFiled;

@Route(value = "goals", layout = MainLayout.class)
@PageTitle("Goals | MoneyLonger")
public class GoalView extends VerticalLayout {
    private final CurrentUserServiceImpl currentUserService;
    private final GoalServiceImpl goalService;
    private final Grid<Goal> grid = new Grid<>(Goal.class);
    private final TextField filterField = textFiled("");
    CheckboxGroup<GoalProgress> checkboxGroup = new CheckboxGroup<>();

    public GoalView(
            CurrentUserServiceImpl currentUserService,
            GoalServiceImpl goalService) {

        this.currentUserService = currentUserService;
        this.goalService = goalService;

        addClassName("goals-view");
        setSizeFull();
        setAlignItems(Alignment.BASELINE);

        configureFilterFields();
        configureGrid();

        add(
                createToolBar(),
                grid
        );

        updateList();
    }

    private void configureFilterFields() {
        filterField.setPlaceholder("Search");
        filterField.setPrefixComponent(new Icon("lumo", "search"));
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.setClearButtonVisible(true);
        filterField.addValueChangeListener(e -> updateList());

        checkboxGroup.setItems(GoalProgress.CURRENT, GoalProgress.COMPLETED, GoalProgress.FAILED);
        checkboxGroup.addSelectionListener(e -> updateList());
    }

    private void updateList() {
        //change for current user eventually
        CurrentUser user = currentUserService.findUserByEmailOrLogin("test user");

        grid.setItems(goalService.findUserGoals(user.getUserId(), filterField.isEmpty() ? null : filterField.getValue(), checkboxGroup.isEmpty() ? Set.of(GoalProgress.values()) : checkboxGroup.getSelectedItems()));
    }

    private void configureGrid() {
        grid.addClassNames("goals-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setColumns();
        grid.addColumn(Goal::getGoalNote).setHeader("Note").setSortable(true).setFlexGrow(2);
        Grid.Column<Goal> progressBar =
                grid.addComponentColumn(this::progressBar).setHeader("Progress").setSortable(false);
        grid.addColumn(this::daysLeft).setHeader("Days left").setSortable(true).setFlexGrow(1);

        progressBar.setFlexGrow(5);

        grid.asSingleSelect().addValueChangeListener(e -> {
            if (grid.getColumns().size() > 3) {
                grid.removeColumnByKey("buttons");
                return;
            }
            showButtons();
        });
    }

    private void showButtons() {
        grid.addComponentColumn(goal -> {
            Button editButton = new Button("Edit");
            editButton.setIcon(new Icon("lumo", "edit"));
            editButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("add-goal/" + goal.getGoalId())));

            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> deleteGoal(goal));


            return new HorizontalLayout(editButton, deleteButton);
        }).setKey("buttons").setHeader("Edit");
    }

    private void deleteGoal(Goal goal) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Are you sure you want to delete this goal permanently?");

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener((e) -> {
            goalService.deleteGoal(goal.getGoalId());
            updateList();
            dialog.close();
        });

        SharedComponents.configureDialog(dialog, deleteButton);
    }


    private Component progressBar(Goal goal) {
        ProgressBar progressBar = new ProgressBar();

        if (goal.getCurrentAmount() >= 0) {
            progressBar.setMin(0);
        } else {
            progressBar.setMin(goal.getCurrentAmount());
        }

        progressBar.setMax(goal.getGoalAmount());
        progressBar.setValue(goal.getCurrentAmount());

        Div progressBarLabel = new Div();

        progressBarLabel.setText("Current progress " + goal.getCurrentAmount() + " of " + goal.getGoalAmount());
        if (goal.getGoalProgress().equals(GoalProgress.COMPLETED)) {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        } else if (goal.getGoalAmount() / goal.getCurrentAmount() < 4) {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_CONTRAST);
        } else {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
        }

        return new VerticalLayout(progressBarLabel, progressBar);
    }

    private String daysLeft(Goal goal) {
        Date today = new Date();
        long timeLeft = goal.getFinishDate().getTime() - today.getTime();

        if (goal.getGoalProgress().equals(GoalProgress.COMPLETED)) {
            return "Success";
        }

        if (timeLeft <= 0) {
            goalService.updateStatus(goal.getGoalId());
            return "Failed";
        }

        return String.valueOf((timeLeft / (1000 * 60 * 60 * 24) + 1));
    }

    private Component createToolBar() {
        Button addGoal = SharedComponents.getAddComponentButton("Add goal", "add-goal");
        addGoal.setWidthFull();

        checkboxGroup.setVisible(false);

        Button statusSearch = new Button("Status");
        statusSearch.setIcon(new Icon("lumo", "checkmark"));
        statusSearch.addClickListener(e -> checkboxGroup.setVisible(!checkboxGroup.isVisible()));
        statusSearch.setWidthFull();

        HorizontalLayout toolbar = new HorizontalLayout(
                addGoal,
                filterField,
                statusSearch,
                checkboxGroup
        );


        toolbar.addClassName("grid-toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setSizeUndefined();

        return toolbar;
    }
}
