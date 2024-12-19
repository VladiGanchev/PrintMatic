import React from "react";
import { useState } from "react";

export default function AdminPanel() {
  const [users, setUsers] = useState([
    { id: 1, name: "Иван Иванов", email: "ivan@example.com", role: "Служител" },
    {
      id: 2,
      name: "Мария Петрова",
      email: "maria@example.com",
      role: "Администратор",
    },
    {
      id: 3,
      name: "Елена Георгиева",
      email: "elena@example.com",
      role: "Служител",
    },
  ]);

  const updateUserRole = (id, newRole) => {
    const updatedUsers = users.map((user) =>
      user.id === id ? { ...user, role: newRole } : user
    );
    setUsers(updatedUsers);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-2xl font-bold mb-6">Административен Панел</h1>
      <div className="bg-white shadow rounded-lg p-4">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr>
              <th className="border-b p-4 font-semibold text-gray-700">Име</th>
              <th className="border-b p-4 font-semibold text-gray-700">
                Имейл
              </th>
              <th className="border-b p-4 font-semibold text-gray-700">Роля</th>
              <th className="border-b p-4 font-semibold text-gray-700">
                Действия
              </th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="border-b p-4">{user.name}</td>
                <td className="border-b p-4">{user.email}</td>
                <td className="border-b p-4">{user.role}</td>
                <td className="border-b p-4">
                  <div className="flex space-x-2">
                    <button
                      onClick={() => updateUserRole(user.id, "Администратор")}
                      className={`p-2 text-white rounded-lg ${
                        user.role === "Администратор"
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-blue-500 hover:bg-blue-600"
                      }`}
                      disabled={user.role === "Администратор"}
                    >
                      Направи администратор
                    </button>
                    <button
                      onClick={() => updateUserRole(user.id, "Служител")}
                      className={`p-2 text-white rounded-lg ${
                        user.role === "Служител"
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-green-500 hover:bg-green-600"
                      }`}
                      disabled={user.role === "Служител"}
                    >
                      Направи служител
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
