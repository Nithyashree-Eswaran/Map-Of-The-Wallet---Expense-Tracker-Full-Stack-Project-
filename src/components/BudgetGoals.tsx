
import React from "react";
import { BudgetGoal } from "../utils/types";
import { cn } from "../lib/utils";

interface BudgetGoalsProps {
  budgetGoals: BudgetGoal[];
}

const BudgetGoals = ({ budgetGoals }: BudgetGoalsProps) => {
  return (
    <div className="space-y-4">
      {budgetGoals.length === 0 ? (
        <p className="text-center text-muted-foreground py-4">
          No budget goals set. Create your first budget goal!
        </p>
      ) : (
        <div className="space-y-4">
          {budgetGoals.map((goal) => {
            const percentage = Math.min(100, (goal.spent / goal.amount) * 100);
            const isOverBudget = goal.spent > goal.amount;
            
            return (
              <div key={goal.id} className="space-y-1">
                <div className="flex justify-between items-center">
                  <span className="font-medium">{goal.category}</span>
                  <span className={cn("text-sm", 
                    isOverBudget ? "text-red-500" : "text-muted-foreground"
                  )}>
                    ${goal.spent.toFixed(2)} / ${goal.amount.toFixed(2)}
                  </span>
                </div>
                <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div 
                    className={cn(
                      "h-full rounded-full", 
                      isOverBudget ? "bg-red-500" : "bg-green-500"
                    )} 
                    style={{ width: `${percentage}%` }}
                  />
                </div>
                {isOverBudget && (
                  <p className="text-xs text-red-500">
                    Over budget by ${(goal.spent - goal.amount).toFixed(2)}
                  </p>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default BudgetGoals;
