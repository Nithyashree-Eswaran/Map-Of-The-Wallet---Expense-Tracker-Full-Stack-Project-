
package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.security.services.UserDetailsImpl;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllExpenses(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        
        String userId = userDetails.getId();
        
        if (category != null && startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            return ResponseEntity.ok(expenseRepository
                    .findByUserIdAndCategoryAndDateBetween(userId, category, start, end));
        } else if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            return ResponseEntity.ok(expenseRepository
                    .findByUserIdAndDateBetween(userId, start, end));
        } else if (category != null) {
            return ResponseEntity.ok(expenseRepository
                    .findByUserIdAndCategory(userId, category));
        }
        
        return ResponseEntity.ok(expenseRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getExpenseById(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable String id) {
        
        String userId = userDetails.getId();
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        // Check if the expense belongs to the current user
        if (!expense.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("Not authorized to access this expense");
        }
        
        return ResponseEntity.ok(expense);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody Expense expense) {
        
        String userId = userDetails.getId();
        expense.setUserId(userId);
        
        Expense savedExpense = expenseRepository.save(expense);
        
        return ResponseEntity.ok(savedExpense);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable String id,
                                          @Valid @RequestBody Expense expenseDetails) {
        
        String userId = userDetails.getId();
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        // Check if the expense belongs to the current user
        if (!expense.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("Not authorized to update this expense");
        }
        
        expense.setAmount(expenseDetails.getAmount());
        expense.setCategory(expenseDetails.getCategory());
        expense.setDescription(expenseDetails.getDescription());
        expense.setDate(expenseDetails.getDate());
        expense.setPaymentMethod(expenseDetails.getPaymentMethod());
        expense.setRecurring(expenseDetails.isRecurring());
        
        Expense updatedExpense = expenseRepository.save(expense);
        
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteExpense(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable String id) {
        
        String userId = userDetails.getId();
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        // Check if the expense belongs to the current user
        if (!expense.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("Not authorized to delete this expense");
        }
        
        expenseRepository.delete(expense);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getExpenseStatistics(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestParam(required = false) String period) {
        
        String userId = userDetails.getId();
        
        // Default to monthly statistics if period is not specified
        if (period == null) {
            period = "monthly";
        }
        
        Map<String, Object> statistics = expenseService.getStatistics(userId, period);
        
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/export")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> exportExpenses(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam(required = false) String format,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        
        String userId = userDetails.getId();
        
        // Default export format is CSV
        if (format == null) {
            format = "csv";
        }
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, start, end);
        
        String exportData = expenseService.exportExpenses(expenses, format);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", exportData);
        response.put("format", format);
        
        return ResponseEntity.ok(response);
    }
}
