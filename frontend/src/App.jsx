import { Routes, Route } from "react-router-dom";
import "./App.css";

import RegisterPage from "./pages/RegisterPage";
import Home from "./pages/home";
import UserHome from "./pages/UserHome";
import UserPayment from "./pages/userPayment";
import UserNotifications from "./pages/userNotifications";
import UserHistory from "./pages/userHistory";
import UserHistoryDocument from "./pages/userHistoryDocument";
import UserProfile from "./pages/userProfile";

function App() {
  return (
    <Routes>
      <Route path="/" element={<RegisterPage />} />

      <Route path="/home" element={<Home />} />

      <Route path="/userHome" element={<UserHome />} />
      <Route path="/userPayment" element={<UserPayment />} />
      <Route path="/userNotifications" element={<UserNotifications />} />
      <Route path="/userHistory" element={<UserHistory />} />
      <Route path="/userHistoryDocument" element={<UserHistoryDocument />} />
      <Route path="/userProfile" element={<UserProfile />} />

      <Route path="*" element={<h1>404 Not Found</h1>} />
    </Routes>
  );
}

export default App;
