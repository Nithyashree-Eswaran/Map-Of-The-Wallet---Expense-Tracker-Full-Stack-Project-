import React from "react";
import { Card, CardContent } from "./ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "./ui/tabs";
import { useExpenses } from "../context/ExpenseContext";
import StatCard from "./StatCard";
import BudgetGoals from "./BudgetGoals";
import SpendingChart from "./SpendingChart";

const Dashboard = () => {
  const { expenses, budgetGoals = [], categories } = useExpenses();

  const totalSpent = expenses.reduce((sum, expense) => sum + expense.amount, 0);
  const totalBudget = budgetGoals.reduce((sum, goal) => sum + goal.amount, 0);
  const remainingBudget = totalBudget - totalSpent;

  return (
    <div className="space-y-6 p-4">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard 
          title="Total Spent" 
          value={`$${totalSpent.toFixed(2)}`} 
          description="Current month" 
          trend={totalSpent > 1000 ? "up" : "down"}
        />
        <StatCard 
          title="Remaining Budget" 
          value={`$${remainingBudget.toFixed(2)}`} 
          description={`of $${totalBudget.toFixed(2)}`}
          trend={remainingBudget > 0 ? "up" : "down"}
        />
        <StatCard 
          title="Expenses" 
          value={expenses.length.toString()} 
          description="Total entries" 
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card>
            <CardContent className="pt-6">
              <Tabs defaultValue="chart">
                <TabsList className="mb-4">
                  <TabsTrigger value="chart">Spending Chart</TabsTrigger>
                  <TabsTrigger value="goals">Budget Goals</TabsTrigger>
                </TabsList>
                <TabsContent value="chart">
                  <SpendingChart expenses={expenses} categories={categories} />
                </TabsContent>
                <TabsContent value="goals">
                  <BudgetGoals budgetGoals={budgetGoals} />
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;