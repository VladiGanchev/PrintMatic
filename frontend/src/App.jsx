import { Routes, Route } from "react-router-dom";
import "./App.css";

import RegisterPage from "./pages/RegisterPage";
import Home from "./pages/home";
import UserHome from "./pages/userHome";
import UserPayment from "./pages/userPayment";
import UserNotifications from "./pages/userNotifications";
import UserHistory from "./pages/userHistory";
import UserProfile from "./pages/userProfile";
import LoginPage from "./pages/LoginPage"
import BalancePayment from "./pages/BalancePayment";
import StripePayment from "./pages/StripePayment";
import EmployeeScreen from "./pages/employeePanel";
import AdminPanel from "./pages/adminPanel";
import UnauthorizedPage from "./pages/UnauthorizedPage";
import { AuthProvider } from "./context/AuthContext";

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<Home />} />

        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/userHome" element={<UserHome />} />
        <Route path="/userPayment" element={<UserPayment />} />
        <Route path="/userNotifications" element={<UserNotifications />} />
        <Route path="/userHistory" element={<UserHistory />} />
        <Route path="/userProfile" element={<UserProfile />} />
        <Route path="/balancePayment" element={<BalancePayment />} />
        <Route path='/stripePayment/:orderId' element={<StripePayment />} />
        <Route path="/employeePanel" element={<EmployeeScreen />} />
        <Route path="/adminPanel" element={<AdminPanel />} />
        <Route path="/unauthorize" element={<UnauthorizedPage />}/>

        <Route path="*" element={<h1>404 Not Found</h1>} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
