
import React from "react";
import { Card, CardContent } from "./ui/card";
import { TrendingUp, TrendingDown } from "lucide-react";

interface StatCardProps {
  title: string;
  value: string;
  description?: string;
  trend?: "up" | "down";
}

const StatCard = ({ title, value, description, trend }: StatCardProps) => {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            <h3 className="text-2xl font-bold mt-1">{value}</h3>
            {description && (
              <p className="text-xs text-muted-foreground mt-1">{description}</p>
            )}
          </div>
          {trend && (
            <div className={
              trend === "up" 
                ? "text-green-500 bg-green-50 p-2 rounded-full" 
                : "text-red-500 bg-red-50 p-2 rounded-full"
            }>
              {trend === "up" ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};

export default StatCard;
