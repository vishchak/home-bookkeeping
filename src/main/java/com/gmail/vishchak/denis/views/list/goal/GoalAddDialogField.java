package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.model.CustomUser;
import com.gmail.vishchak.denis.model.Goal;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.GoalServiceImpl;
import com.gmail.vishchak.denis.views.list.shared.SharedComponents;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.*;

@PermitAll
@Route("add-goal")
@PageTitle("Goal | FROG-STOCK")
@CssImport("./themes/flowcrmtutorial/components/form/transaction-goal-form.css")
public class GoalAddDialogField extends Composite<VerticalLayout> implements HasUrlParameter<Long> {
    private final GoalServiceImpl goalService;
    private final TextField goalNote = textFiled("Note");
    private final NumberField goalAmount = amountField("Amount");
    private final String format = "dd-MM-yyyy";
    private final DatePicker goalFinishDate = dateField(format, "Finish date");
    private final CustomUser user;

    public GoalAddDialogField(GoalServiceImpl goalService, SecurityService securityService) {
        this.goalService = goalService;
        this.user = securityService.getAuthenticatedUser();

        configureFields();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter Long goalId) {
        if (goalId == null) {
            formCreate(null, "Add goal");
        } else {
            formCreate(goalId, "Update goal");
        }
    }

    private void formCreate(Long goalId, String header) {

        VerticalLayout content = new VerticalLayout
                (
                        new H2(header),
                        goalNote,
                        goalAmount,
                        goalFinishDate,
                        SharedComponents.createButtonsLayout
                                (
                                        goalId,
                                        "goals",
                                        this::validateAndAdd,
                                        (id) -> validateAndUpdate(goalId),
                                        UI.getCurrent()
                                )
                );

        if (goalId != null) {
            configureUpdateGoal(goalId);
        }

        content.addClassName("transaction-goal-form-content");

        VerticalLayout layout = getContent();

        layout.add(content);
        layout.addClassName("transaction-goal-form-layout");
        layout.setSizeFull();
    }

    private void configureFields() {
        goalNote.setRequired(true);

        goalFinishDate.setRequired(true);
        goalFinishDate.setMin(LocalDate.now());
        goalFinishDate.setMax(LocalDate.MAX);
    }

    private void configureUpdateGoal(Long goalId) {
        Optional<Goal> optionalGoal = goalService.findById(goalId);
        optionalGoal.ifPresent(g -> {
            goalNote.setValue(g.getGoalNote());
            goalAmount.setValue(g.getGoalAmount());

            LocalDate dialogDate = new Date(g.getFinishDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (dialogDate.isBefore(LocalDate.now())) {
                goalFinishDate.setMin(dialogDate);
            }

            goalFinishDate.setValue(dialogDate);
        });

    }

    private void validateAndAdd() {
        try {
            getUI().ifPresent(ui -> {
                goalService.addGoal(new Goal(goalNote.getValue(), goalAmount.getValue(),
                        Date.from(goalFinishDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        user));
                ui.navigate("goals");
            });
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            ErrorNotification();
        }
    }

    private void validateAndUpdate(Long id) {
        try {
            getUI().ifPresent(ui -> {
                goalService.updateGoal(id,
                        goalNote.getValue(), goalAmount.getValue(),
                        Date.from(goalFinishDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );

                ui.navigate("goals");
            });
        } catch (NullPointerException | javax.validation.ConstraintViolationException e) {
            ErrorNotification();
        }
    }
}
