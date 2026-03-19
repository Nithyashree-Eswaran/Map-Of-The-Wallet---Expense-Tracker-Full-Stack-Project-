
package com.expensetracker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "budget_goals")
public class BudgetGoal {
    
    @Id
    private String id;
    
    private String userId;
    
    private String category;
    
    private double amount;
    
    private double spent = 0.0;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private boolean recurring = false;
    
    private String recurringPeriod; // monthly, weekly, yearly
    
    // Constructors
    public BudgetGoal() {
    }
    
    public BudgetGoal(String userId, String category, double amount, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getSpent() {
        return spent;
    }
    
    public void setSpent(double spent) {
        this.spent = spent;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public boolean isRecurring() {
        return recurring;
    }
    
    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
    
    public String getRecurringPeriod() {
        return recurringPeriod;
    }
    
    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }
    
    // Helper method to calculate remaining budget
    public double getRemainingBudget() {
        return amount - spent;
    }
    
    // Helper method to calculate percentage of budget used
    public double getPercentageUsed() {
        if (amount == 0) {
            return 0.0;
        }
        return (spent / amount) * 100;
    }
}
