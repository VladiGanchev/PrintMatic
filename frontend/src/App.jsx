import { Routes, Route } from "react-router-dom";
import "./App.css";

import RegisterPage from "./pages/RegisterPage";
import Home from "./pages/home";

function App() {
  return (
    <Routes>
      <Route path="/" element={<RegisterPage />} />
      <Route path="/home" element={<Home />} />
    </Routes>
  );
}

export default App;
