import { useState } from "react";
import HeaderUser from "../components/headerUser";

export default function UserProfile() {
  const [name, setName] = useState("Петър Петров");
  const [phone, setPhone] = useState("0888888888");
  const [email, setEmail] = useState("p.petrov@example.com");

  const handleSave = () => {
    // Logic to save changes (e.g., API call) can be added here
    alert("Промените са запазени успешно!");
  };

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
              <p className="text-3xl font-bold">{name}</p>
              <p className="text-md">
                Баланс: <b>500 лв.</b>
              </p>
            </div>
          </div>
          <button className="bg-green-500 text-white rounded-md p-2 h-12 w-48 hover:bg-green-600">
            Добавете средства
          </button>
        </div>
        <div className="mt-12 space-y-6 w-1/2">
          {/* Name Field */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Име:
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full p-2 border rounded-md"
            />
          </div>
          {/* Phone Field */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Телефонен номер:
            </label>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full p-2 border rounded-md"
            />
          </div>
          {/* Email Field */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Имейл адрес:
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-2 border rounded-md"
            />
          </div>
          {/* Save Changes Button */}
          <button
            onClick={handleSave}
            className="bg-primary text-white rounded-md p-2 h-12 w-48 hover:bg-blue-900"
          >
            Запазете промените
          </button>
        </div>
      </div>
    </div>
  );
}
