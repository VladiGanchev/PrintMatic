import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser, loginUser } from "../services/authService";

const RegisterPage = () => {
  let navigate = useNavigate()
  const [userData, setUserData] = useState({
    email: "",
    password: "",
  });

  const [errorMessage, setErrorMessage] = useState("");

  const handleLogin = async () => {
    try {
      const data = await loginUser(userData);
      navigate('/userHome')
      console.log("User logged in successfully:", data);
      window.location.reload()
    } catch (error) {
      console.error("Login error: ", error);
      setErrorMessage(
        error.message ? error : "Login failed"
      );
    }
  };

  return (
    <div className="max-h-screen h-[90vh] flex justify-center items-center w-full">
      <div className="flex flex-col justify-start items-start bg-primary text-white text-md py-4 px-6 rounded-lg">
        <p className="text-lg">Login</p>
        <div className='flex text-s mb-6 space-x-2'>
                    <p className='text-gray-500'>New user?</p>
                    <p className='cursor-pointer hover:text-gray-300' onClick={() => navigate('/register')}>Create account</p>
                </div>
        <div className="flex flex-row gap-x-8">
          <div className="flex flex-col  w-full items-center text-gray-500 space-y-4 pt-6">
            <input
              type="email"
              className="w-full bg-white p-2 rounded-md"
              placeholder="Email "
              value={userData.email}
              onChange={(e) =>
                setUserData({ ...userData, email: e.target.value })
              }
            />
            <input
              type="password"
              className="w-full bg-white p-2 rounded-md"
              placeholder="Password"
              value={userData.password}
              onChange={(e) =>
                setUserData({ ...userData, password: e.target.value })
              }
            />
          </div>
        </div>
        <div className="w-full flex flex-row justify-center">
          <div
            className="text-center w-1/2 bg-white text-primary font-semibold my-10 cursor-pointer hover:bg-gray-100 rounded-md"
            onClick={handleLogin}
          >
            Login
          </div>
        </div>
        {errorMessage && <p className="text-red-500 text-sm">{errorMessage}</p>}
      </div>
    </div>
  );
};

export default RegisterPage;
