import React from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import {
  SidebarProvider,
  Sidebar,
  SidebarContent,
  SidebarTrigger,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
} from "./ui/sidebar";
import {
  LayoutDashboard,
  CreditCard,
  BarChart2,
  User,
  Settings,
  PlusCircle,
  LogOut,
} from "lucide-react";

// ✅ Add the prop type
interface LayoutProps {
  onLogout: () => void;
}

const Layout: React.FC<LayoutProps> = ({ onLogout }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const currentPath = location.pathname;

  const isActive = (path: string) => currentPath === path;

  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full">
        <Sidebar variant="floating" side="left">
          <SidebarContent>
            <SidebarGroup>
              <SidebarGroupLabel className="text-md">Navigation</SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Dashboard"
                      isActive={isActive("/dashboard")}
                      onClick={() => navigate("/dashboard")}
                    >
                      <LayoutDashboard className="mr-2" />
                      <span>Dashboard</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Add Expense"
                      isActive={isActive("/add-expense")}
                      onClick={() => navigate("/add-expense")}
                    >
                      <PlusCircle className="mr-2" />
                      <span>Add Expense</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Statistics"
                      isActive={isActive("/statistics")}
                      onClick={() => navigate("/statistics")}
                    >
                      <BarChart2 className="mr-2" />
                      <span>Statistics</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Expenses"
                      isActive={isActive("/expenses")}
                      onClick={() => navigate("/expenses")}
                    >
                      <CreditCard className="mr-2" />
                      <span>Expenses</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>

            <SidebarGroup>
              <SidebarGroupLabel>User</SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Profile"
                      isActive={isActive("/profile")}
                      onClick={() => navigate("/profile")}
                    >
                      <User className="mr-2" />
                      <span>Profile</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Settings"
                      isActive={isActive("/settings")}
                      onClick={() => navigate("/settings")}
                    >
                      <Settings className="mr-2" />
                      <span>Settings</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>

                  <SidebarMenuItem>
                    <SidebarMenuButton
                      tooltip="Logout"
                      onClick={onLogout} 
                    >
                      <LogOut className="mr-2" />
                      <span>Logout</span>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </SidebarContent>
        </Sidebar>

        <main className="flex-1 overflow-auto">
          <div className="p-6">
            <SidebarTrigger className="mb-4" />
            <Outlet />
          </div>
        </main>
      </div>
    </SidebarProvider>
  );
};

export default Layout;
