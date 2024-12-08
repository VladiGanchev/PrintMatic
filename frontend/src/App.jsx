import { Routes, Route } from "react-router-dom";
import "./App.css";

import RegisterPage from "./pages/RegisterPage";
import Home from "./pages/home";
import UserHome from "./pages/UserHome";
import UserPayment from "./pages/userPayment";
import UserNotifications from "./pages/userNotifications";

function App() {
  return (
    <Routes>
      <Route path="/" element={<RegisterPage />} />
      <Route path="/home" element={<Home />} />
      <Route path="/userHome" element={<UserHome />} />
      <Route path="/userPayment" element={<UserPayment />} />
      <Route path="/userNotifications" element={<UserNotifications />} />
    </Routes>
  );
}

export default App;
