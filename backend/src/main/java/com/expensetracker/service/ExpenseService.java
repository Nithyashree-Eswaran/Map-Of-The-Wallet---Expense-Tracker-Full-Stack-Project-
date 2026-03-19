
package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Map<String, Object> getStatistics(String userId, String period) {
        Map<String, Object> statistics = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        
        switch (period.toLowerCase()) {
            case "weekly":
                startDate = today.minusWeeks(1);
                break;
            case "monthly":
                startDate = today.minusMonths(1);
                break;
            case "yearly":
                startDate = today.minusYears(1);
                break;
            case "all":
                startDate = LocalDate.of(2000, 1, 1); // Far back enough to include all data
                break;
            default:
                startDate = today.minusMonths(1); // Default to monthly
                break;
        }
        
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, today);
        
        // Total amount spent
        double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
        statistics.put("totalAmount", totalAmount);
        
        // Expense count
        statistics.put("expenseCount", expenses.size());
        
        // Average expense
        double averageExpense = expenses.size() > 0 ? totalAmount / expenses.size() : 0;
        statistics.put("averageExpense", averageExpense);
        
        // Category breakdown
        Map<String, Double> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
        statistics.put("categoryBreakdown", categoryBreakdown);
        
        // Payment method breakdown
        Map<String, Double> paymentMethodBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getPaymentMethod,
                        Collectors.summingDouble(Expense::getAmount)
                ));
        statistics.put("paymentMethodBreakdown", paymentMethodBreakdown);
        
        // Time series data for charts
        Map<String, Double> timeSeriesData = new LinkedHashMap<>();
        
        if ("weekly".equalsIgnoreCase(period)) {
            // Daily breakdown for the last week
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dayLabel = date.format(DateTimeFormatter.ofPattern("EEE"));
                
                final int dayOffset = i;
                double amount = expenses.stream()
                        .filter(e -> e.getDate().isEqual(today.minusDays(dayOffset)))
                        .mapToDouble(Expense::getAmount)
                        .sum();
                
                timeSeriesData.put(dayLabel, amount);
            }
        } else if ("monthly".equalsIgnoreCase(period)) {
            // Weekly breakdown for the last month
            for (int i = 4; i >= 0; i--) {
                LocalDate weekStart = today.minusWeeks(i);
                LocalDate weekEnd = weekStart.plusDays(6);
                
                String weekLabel = "Week " + (5 - i);
                
                final int weekOffset = i;
                double amount = expenses.stream()
                        .filter(e -> {
                            LocalDate date = e.getDate();
                            LocalDate currentWeekStart = today.minusWeeks(weekOffset);
                            LocalDate currentWeekEnd = currentWeekStart.plusDays(6);
                            return (date.isEqual(currentWeekStart) || date.isAfter(currentWeekStart)) 
                                   && (date.isEqual(currentWeekEnd) || date.isBefore(currentWeekEnd));
                        })
                        .mapToDouble(Expense::getAmount)
                        .sum();
                
                timeSeriesData.put(weekLabel, amount);
            }
        } else {
            // Monthly breakdown for the last year
            for (int i = 11; i >= 0; i--) {
                LocalDate date = today.minusMonths(i);
                String monthLabel = date.getMonth().toString().substring(0, 3);
                
                final int monthOffset = i;
                final Month currentMonth = date.getMonth();
                
                double amount = expenses.stream()
                        .filter(e -> e.getDate().getMonth() == currentMonth)
                        .mapToDouble(Expense::getAmount)
                        .sum();
                
                timeSeriesData.put(monthLabel, amount);
            }
        }
        
        statistics.put("timeSeriesData", timeSeriesData);
        
        // Highest expense
        Optional<Expense> highestExpense = expenses.stream()
                .max(Comparator.comparing(Expense::getAmount));
        
        if (highestExpense.isPresent()) {
            Map<String, Object> highestExpenseData = new HashMap<>();
            highestExpenseData.put("amount", highestExpense.get().getAmount());
            highestExpenseData.put("category", highestExpense.get().getCategory());
            highestExpenseData.put("date", highestExpense.get().getDate().toString());
            highestExpenseData.put("description", highestExpense.get().getDescription());
            
            statistics.put("highestExpense", highestExpenseData);
        }
        
        return statistics;
    }
    
    public String exportExpenses(List<Expense> expenses, String format) {
        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(expenses);
        } else if ("json".equalsIgnoreCase(format)) {
            return exportToJson(expenses);
        } else {
            return "Unsupported format";
        }
    }
    
    private String exportToCsv(List<Expense> expenses) {
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // Header
            String[] header = {"Date", "Amount", "Category", "Description", "Payment Method", "Recurring"};
            csvWriter.writeNext(header);
            
            // Data
            for (Expense expense : expenses) {
                String[] data = {
                    expense.getDate().toString(),
                    String.valueOf(expense.getAmount()),
                    expense.getCategory(),
                    expense.getDescription(),
                    expense.getPaymentMethod(),
                    String.valueOf(expense.isRecurring())
                };
                csvWriter.writeNext(data);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        
        return stringWriter.toString();
    }
    
    private String exportToJson(List<Expense> expenses) {
        // Simple JSON export without using full Jackson serialization
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            json.append("  {\n");
            json.append("    \"id\": \"").append(expense.getId()).append("\",\n");
            json.append("    \"date\": \"").append(expense.getDate()).append("\",\n");
            json.append("    \"amount\": ").append(expense.getAmount()).append(",\n");
            json.append("    \"category\": \"").append(expense.getCategory()).append("\",\n");
            json.append("    \"description\": \"").append(expense.getDescription().replace("\"", "\\\"")).append("\",\n");
            json.append("    \"paymentMethod\": \"").append(expense.getPaymentMethod()).append("\",\n");
            json.append("    \"recurring\": ").append(expense.isRecurring()).append("\n");
            json.append("  }");
            
            if (i < expenses.size() - 1) {
                json.append(",\n");
            } else {
                json.append("\n");
            }
        }
        
        json.append("]");
        return json.toString();
    }
}
