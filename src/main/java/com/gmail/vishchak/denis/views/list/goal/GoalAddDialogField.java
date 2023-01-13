package com.gmail.vishchak.denis.views.list.goal;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.security.SecurityService;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static com.gmail.vishchak.denis.views.list.shared.SharedComponents.*;

@Route("add-goal")
@PageTitle("Transaction")
@PermitAll
public class GoalAddDialogField extends Div implements HasUrlParameter<Long> {
    private final GoalServiceImpl goalService;
    private final Dialog dialog = new Dialog();
    private final Binder<Goal> binder = new BeanValidationBinder<>(Goal.class);
    private final TextField goalNote = textFiled("Note");
    private final NumberField goalAmount = amountField("Amount");
    private final String format = "dd-MM-yyyy";
    private final DatePicker goalFinishDate = dateField(format, "Finish date");
    private final CurrentUser user;

    public GoalAddDialogField(GoalServiceImpl goalService, SecurityService securityService) {
        this.goalService = goalService;
        this.user = securityService.getAuthenticatedUser();

        goalNote.setRequired(true);

        goalFinishDate.setRequired(true);
        goalFinishDate.setMin(LocalDate.now());
        goalFinishDate.setMax(LocalDate.MAX);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent,
                             @OptionalParameter Long id) {
        if (id == null) {
            dialogCreate("Add goal", null, "add");
            return;
        }
        dialogCreate("Update goal", id, "update");
    }

    private void dialogCreate(String header, Long id, String buttonText) {
        dialog.setHeaderTitle(header);
        dialog.setCloseOnOutsideClick(false);

        binder.bindInstanceFields(this);

        if (id != null) {
            Optional<Goal> optionalGoal = goalService.findById(id);
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

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button confirmButton = createConfirmButton(buttonText);
        if (id == null) {
            confirmButton.addClickListener(e -> validateAndAdd());
        } else {
            confirmButton.addClickListener(e -> validateAndUpdate(id));
        }

        Button cancelButton = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate("goals")));
        cancelButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(new HorizontalLayout(cancelButton, confirmButton));

        add(dialog);
        dialog.open();
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

    private VerticalLayout createDialogLayout() {
        VerticalLayout dialogLayout = new VerticalLayout(goalNote, goalAmount, goalFinishDate);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}
