import { useEffect, useState } from "react";
import { getLoginUser, updateUser, depositBalance } from "../services/userService"
import HeaderUser from "../components/headerUser";
import PaymentModal from "../components/PaymentModal";

export default function UserProfile() {
  const [openModal, setOpenModal] = useState(false)
  const [submittedAmount, setSubmittedAmount] = useState(null);
  const [updateData, setUpdateData] = useState({
    firstName: "",
    lastName: "",
    phoneNumber: "",
    email: ""
  });
  const [balance, setBalance] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const userData = await getLoginUser();
        setUpdateData({
          firstName: userData.firstName,
          lastName: userData.lastName,
          phoneNumber: userData.phoneNumber,
          email: userData.email
        });
        setBalance(userData.balance);
      } catch (err) {
        setError(err.message);
        console.error("Error fetching user data:", err);
      } finally {
        setIsLoading(false);
      }
    }

    fetchUserData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUpdateData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSave = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await updateUser(updateData);
    } catch (err) {
      setError(err.message);
      console.error("Error updating user:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
  };

  const handleSubmit = async(amount) => {
    if (!amount || parseFloat(amount) <= 0) {
      alert("Please enter a valid amount greater than 0.");
      return;
    }
    setSubmittedAmount(amount);
    try {
      const sessionForBalance = await depositBalance(amount); 
      const stripePaymentURL = sessionForBalance?.stripePaymentURL;
      localStorage.setItem('session', sessionForBalance.sessionId)
      if (stripePaymentURL) {
        window.location.href = stripePaymentURL;
      } else {
        alert("Error: Stripe payment URL not found.");
      }
    } catch (error) {
      console.error("Error processing payment:", error);
      alert("An error occurred while processing your payment.");
    }
  };

  const formatPrice = (number) => {
    return new Intl.NumberFormat('bg-BG', {
      style: 'currency',
      currency: 'BGN'
    }).format(number);
  }


  if (isLoading) {
    return (
      <div className="w-screen h-screen flex flex-row hover:cursor-default">
        <HeaderUser />
        <div className="w-full h-screen flex justify-center items-center">
          <p className="text-2xl">Зареждане...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen overflow-scroll flex flex-col p-12">
        <p className="text-5xl font-bold">Потребителски профил</p>
        <div className="w-full justify-between flex items-center">
          <div className="flex flex-row mt-8 items-center">
            <img
              src="/images/profile.webp"
              alt="user"
              className="w-36 rounded-full drop-shadow-lg shadow-black"
            />
            <div className="flex flex-col ml-4">
              <p className="text-3xl font-bold">{updateData.firstName} {updateData.lastName}</p>
              <p className="text-md">
                Баланс: <b>{formatPrice(balance)}</b>
              </p>
            </div>
          </div>
          <button className="bg-green-500 text-white rounded-md p-2 h-12 w-48 hover:bg-green-600"
                  onClick={handleOpenModal}>
            Добавете средства
          </button>
        </div>

        <div className="mt-12 space-y-6 w-1/2">
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Име:
            </label>
            <input
              type="text"
              name="firstName"
              value={updateData.firstName}
              onChange={handleInputChange}
              className="w-full p-2 border rounded-md"
            />
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Фамилия:
            </label>
            <input
              type="text"
              name="lastName"
              value={updateData.lastName}
              onChange={handleInputChange}
              className="w-full p-2 border rounded-md"
            />
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Телефонен номер:
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={updateData.phoneNumber}
              onChange={handleInputChange}
              className="w-full p-2 border rounded-md"
            />
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Имейл адрес:
            </label>
            <input
              type="email"
              name="email"
              value={updateData.email}
              onChange={handleInputChange}
              className="w-full p-2 border rounded-md"
            />
          </div>

          {error && (
            <div className="mt-4 p-4 bg-red-100 text-red-600 rounded-md">
              {error}
            </div>
          )}

          <button
            onClick={handleSave}
            disabled={isLoading}
            className={`bg-primary text-white rounded-md p-2 h-12 w-48 
              ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:bg-blue-900'}`}
          >
            {isLoading ? 'Запазване...' : 'Запазете промените'}
          </button>
        </div>
      </div>
      <PaymentModal  
        isOpen={openModal}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}/>
    </div>
  );
}