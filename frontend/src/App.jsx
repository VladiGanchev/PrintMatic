import { Routes, Route } from "react-router-dom";
import "./App.css";

import RegisterPage from "./pages/RegisterPage";
import Home from "./pages/home";
import UserHome from "./pages/UserHome";
import UserPayment from "./pages/userPayment";

function App() {
  return (
    <Routes>
      <Route path="/" element={<RegisterPage />} />
      <Route path="/home" element={<Home />} />
      <Route path="/userHome" element={<UserHome />} />
      <Route path="/userPayment" element={<UserPayment />} />
    </Routes>
  );
}

export default App;
