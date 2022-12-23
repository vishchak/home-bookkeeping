package com.gmail.vishchak.denis.testdata;

import com.gmail.vishchak.denis.model.*;
import com.gmail.vishchak.denis.service.*;
import com.vaadin.flow.spring.annotation.SpringComponent;


import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringComponent
public class DataGenerator {
    private final CurrentUserServiceImpl currentUserService;
    private final AccountServiceImpl accountService;
    private final CategoryServiceImpl categoryService;
    private final SubcategoryServiceImpl subcategoryService;


    public DataGenerator(CurrentUserServiceImpl currentUserService,
                         AccountServiceImpl accountService,
                         CategoryServiceImpl categoryService,
                         SubcategoryServiceImpl subcategoryService) {
        this.currentUserService = currentUserService;
        this.accountService = accountService;
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

        if(categoryExpense.isPresent() && categoryIncome.isPresent() && categoryOther.isPresent()) {
            subcategoryService.addSubcategories(
                    new Subcategory("Food & Beverage", categoryExpense.get()),
                    new Subcategory("Transportation", categoryExpense.get()),
                    new Subcategory("Rentals", categoryExpense.get()),
                    new Subcategory("Water bill", categoryExpense.get()),
                    new Subcategory("Phone bill", categoryExpense.get()),
                    new Subcategory("Gas bill", categoryExpense.get()),
                    new Subcategory("Television bill", categoryExpense.get()),
                    new Subcategory("Collect interest", categoryIncome.get()),
                    new Subcategory("Salary", categoryIncome.get()),
                    new Subcategory("Other income", categoryIncome.get()),
                    new Subcategory("Incoming transfers", categoryIncome.get()),
                    new Subcategory("Debt collection", categoryOther.get()),
                    new Subcategory("Debt", categoryOther.get()),
                    new Subcategory("Loan", categoryOther.get()),
                    new Subcategory("Repayment", categoryOther.get())
            );
        }

        currentUserService.addUser("test user", "pass", "test mail", null);
        accountService.addAccount(new Account("test account", 200D, currentUserService.findUserByEmailOrLogin("test user")));


    }
}
