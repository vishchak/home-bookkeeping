package com.gmail.vishchak.denis.data;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.spring.annotation.SpringComponent;


import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringComponent
public class DataGenerator {
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;

    public DataGenerator(CategoryServiceImpl categoryService,
                         SubcategoryServiceImpl subcategoryService) {
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }

    @PostConstruct
    public void loadData() {
        Category expense = new Category("Expense");
        Category income = new Category("Income");
        Category other = new Category("Other");
        categoryService.addCategories(expense, income, other);

        Optional<Category> categoryExpense = categoryService.findCategoryById(1L);
        Optional<Category> categoryIncome = categoryService.findCategoryById(2L);
        Optional<Category> categoryOther = categoryService.findCategoryById(3L);

        if (categoryExpense.isPresent() && categoryIncome.isPresent() && categoryOther.isPresent()) {
            subcategoryService.addSubcategories(
                    new Subcategory("Food & Beverage", categoryExpense.get()),
                    new Subcategory("Transportation", categoryExpense.get()),
                    new Subcategory("Rentals", categoryExpense.get()),
                    new Subcategory("Water bill", categoryExpense.get()),
                    new Subcategory("Phone bill", categoryExpense.get()),
                    new Subcategory("Gas bill", categoryExpense.get()),
                    new Subcategory("Television bill", categoryExpense.get()),
                    new Subcategory("Internet bill", categoryExpense.get()),
                    new Subcategory("Other utility bills", categoryExpense.get()),
                    new Subcategory("Medical checkup", categoryExpense.get()),
                    new Subcategory("Insurances", categoryExpense.get()),
                    new Subcategory("Education", categoryExpense.get()),
                    new Subcategory("Housewares", categoryExpense.get()),
                    new Subcategory("Personal items", categoryExpense.get()),
                    new Subcategory("Home services", categoryExpense.get()),
                    new Subcategory("Other expense", categoryExpense.get()),
                    new Subcategory("Fitness", categoryExpense.get()),
                    new Subcategory("Makeup", categoryExpense.get()),
                    new Subcategory("Gifts & Donations", categoryExpense.get()),
                    new Subcategory("Streaming services", categoryExpense.get()),
                    new Subcategory("Fun money", categoryExpense.get()),
                    new Subcategory("Investment", categoryExpense.get()),
                    new Subcategory("Pay interest", categoryExpense.get()),
                    new Subcategory("Outgoing transfer", categoryExpense.get()),
                    new Subcategory("Goal", categoryExpense.get()),

                    new Subcategory("Collect interest", categoryIncome.get()),
                    new Subcategory("Salary", categoryIncome.get()),
                    new Subcategory("Other income", categoryIncome.get()),
                    new Subcategory("Incoming transfers", categoryIncome.get()),

                    new Subcategory("Debt", categoryOther.get()),
                    new Subcategory("Loan", categoryOther.get()),
                    new Subcategory("Debt repayment", categoryOther.get()),
                    new Subcategory("Loan collection", categoryOther.get())
            );
        }
    }
}
