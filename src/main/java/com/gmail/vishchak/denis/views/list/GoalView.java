package com.gmail.vishchak.denis.views.list;

import com.gmail.vishchak.denis.model.CurrentUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.service.*;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Date;

@Route(value = "goals", layout = MainLayout.class)
@PageTitle("Goals | MoneyLonger")
public class GoalView extends VerticalLayout {
    private final CurrentUserServiceImpl currentUserService;

    private final GoalServiceImpl goalService;
    private final Grid<Goal> grid = new Grid<>(Goal.class);

    public GoalView(
            CurrentUserServiceImpl currentUserService,
            GoalServiceImpl goalService) {

        this.currentUserService = currentUserService;
        this.goalService = goalService;

        addClassName("goals-view");
        setSizeFull();

        configureGrid();

        add(
                addContent()
        );

        updateList();
    }

    private void updateList() {
        //change for current user eventually
        CurrentUser user = currentUserService.findUserByEmailOrLogin("test user");
//add if completed filter
        grid.setItems(goalService.findUserGoals(user.getUserId(), null));
    }

    private Component addContent() {
        VerticalLayout content = new VerticalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();

        return content;
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

       SharedComponents.configureDialog(dialog,deleteButton);
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
        if (goal.getIfCompleted()) {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        } else if (goal.getGoalAmount() / goal.getCurrentAmount() < 4) {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_CONTRAST);
        } else {
            progressBar.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
        }

        return new VerticalLayout(progressBarLabel, progressBar);
    }

    private int daysLeft(Goal goal) {
        Date today = new Date();
        long timeLeft = goal.getFinishDate().getTime() - today.getTime();

        return (int) (timeLeft / (1000 * 60 * 60 * 24) + 1);
    }

}
