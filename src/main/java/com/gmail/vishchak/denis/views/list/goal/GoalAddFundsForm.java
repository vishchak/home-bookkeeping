package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.model.Account;
import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.enums.GoalProgress;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.AccountServiceImpl;
import com.gmail.vishchak.denis.service.GoalServiceImpl;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PermitAll
@Route("add-funds-goal")
@PageTitle("Goal | FROG-STOCK")
@CssImport("./themes/frog-stock/components/form/transaction-goal-form.css")
public class GoalAddFundsForm extends Composite<VerticalLayout> implements HasUrlParameter<Long> {

    private final GoalServiceImpl goalService;
    private final AccountServiceImpl accountService;

    private final ComboBox<Account> accountComboBox = new ComboBox<>("Withdraw from");

    private final NumberField addAmount = new NumberField("Amount to be added");

    private final CustomUser user;

    public GoalAddFundsForm(GoalServiceImpl goalService, AccountServiceImpl accountService, SecurityService securityService) {
        this.goalService = goalService;
        this.accountService = accountService;
        this.user = securityService.getAuthenticatedUser();

        labelGenerator();
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Long goalId) {
        SharedComponents.checkGoalUser(goalId, goalService, user);
        formCreate(goalId);
    }

    private void labelGenerator() {
        accountComboBox.setItems(accountService.findAccountsByUser(user));
        accountComboBox.setItemLabelGenerator(Account::getAccountName);
        accountComboBox.setRequired(true);
    }


    private void formCreate(Long goalId) {
        goalService.findById(goalId).ifPresent(g -> {
            VerticalLayout content = new VerticalLayout
                    (
                            new H2(g.getGoalNote()),
                            addAmount,
                            accountComboBox,
                            SharedComponents.createButtonsLayout
                                    (
                                            goalId,
                                            "goals",
                                            null,
                                            (id) -> addFunds(goalId),
                                            UI.getCurrent()
                                    )
                    );

            content.addClassName("transaction-goal-form-content");

            VerticalLayout layout = getContent();

            layout.add(content);
            layout.addClassName("transaction-goal-form-layout");
            layout.setSizeFull();
        });
    }

    private void addFunds(Long goalId) {
        try {
            goalService.findById(goalId).ifPresent(g -> {
                        if (!g.getGoalProgress().equals(GoalProgress.CURRENT)) {
                            UI.getCurrent().getPage().open("goals", "_self");
                            return;
                        }

                        if (addAmount.getValue() == null) {
                            throw new NullPointerException();
                        }

                        goalService.addMoney(goalId, addAmount.getValue(), accountComboBox.getValue());
                        Notification.show("Amount's been added", 3000, Notification.Position.BOTTOM_START);
                        UI.getCurrent().navigate("goals");
                    }
            );
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            Notification.show("Fill all the necessary fields", 3000, Notification.Position.BOTTOM_START);
        }
    }
}
