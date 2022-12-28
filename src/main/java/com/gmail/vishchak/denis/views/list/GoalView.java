package com.gmail.vishchak.denis.views.list;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "goals", layout = MainLayout.class)
@PageTitle("Goals | MoneyLonger")
public class GoalView extends VerticalLayout {
    public GoalView() {
    }
}
