import React from "react";
import { useState } from "react";
import toast, { Toaster } from 'react-hot-toast';
import { grantRoleToUser, searchByEmail, removeRoleToUser } from "../services/adminService";

export default function AdminPanel() {
  const [users, setUsers] = useState([])


  const handleSearch = async (e) => {
    const query = e.target.value
    try {
      const listOfUsers = await searchByEmail(query)
      setUsers(listOfUsers)
    } catch (error) {
      console.error("Error while searching in admin panel:", error);
    }
  }

  const translateRoles = (role) => {
    const roles = {
      USER: 'Клиент',
      EMPLOYEE: 'Служител',
      ADMIN: 'Администратор'
    };
    return roles[role] || role;
  };

  const updateUserRole = async(user, role) => {
    try {
      if(user.roles.includes(role)){
        const response = await removeRoleToUser(user.email, role)
        toast.success('Успешно премахнахте роля ' + translateRoles(role));
      }else{
        const response = await grantRoleToUser(user.email, role)
        toast.success('Успешно добавихте роля ' + translateRoles(role));
      }
    } catch (error) {
      console.error("Error while granting user role in admin panel:", error);
    }
  }

  return (
    <div className="flex flex-col space-y-4 min-h-screen bg-gray-100 p-6">
      <h1 className="text-2xl font-bold mb-6">Административен Панел</h1>
      <input className="w-1/3 h-10 rounded-md"
        type="text"
        name="search"
        placeholder="Търси по имейл..."

        onChange={handleSearch} />
      <div className="bg-white shadow rounded-lg p-4">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr>
              <th className="border-b p-4 font-semibold text-gray-700">Име</th>
              <th className="border-b p-4 font-semibold text-gray-700">Фамилия</th>
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
                <td className="border-b p-4">{user.firstName}</td>
                <td className="border-b p-4">{user.lastName}</td>
                <td className="border-b p-4">{user.email}</td>
                <td className="border-b p-4">{user.roles.map(r => translateRoles(r)).join(", ")}</td>
                <td className="border-b p-4">
                  <div className="flex space-x-2">
                    <button
                      onClick={() => updateUserRole(user, "ADMIN")}
                      className={`p-2 text-white rounded-lg ${user.roles.includes('ADMIN')
                        ? "bg-red-500 hover:bg-red-600"
                        : "bg-blue-500 hover:bg-blue-600"
                        }`}
                      disabled={user.role === "Администратор"}
                    >
                      {user.roles.includes('ADMIN') ? 'Премахни администратор':'Направи администратор'}
                    </button>
                    <button
                      onClick={() => updateUserRole(user, "EMPLOYEE")}
                      className={`p-2 text-white rounded-lg ${user.roles.includes('EMPLOYEE')
                        ? "bg-red-500 hover:bg-red-600"
                        : "bg-green-500 hover:bg-green-600"
                        }`}
                      disabled={user.role === "Служител"}
                    >
                      {user.roles.includes('EMPLOYEE') ? 'Премахни служител':'Направи служител'}
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
