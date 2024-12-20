import React from "react";
import { useState } from "react";

export default function EmployeeScreen() {
  const [orders, setOrders] = useState([
    { id: 1, title: "Документ 1", status: "Ново", customer: "Иван Иванов" },
    {
      id: 2,
      title: "Документ 2",
      status: "В процес",
      customer: "Мария Петрова",
    },
    { id: 3, title: "Документ 3", status: "Ново", customer: "Елена Георгиева" },
  ]);

  const updateOrderStatus = (id, newStatus) => {
    const updatedOrders = orders.map((order) =>
      order.id === id ? { ...order, status: newStatus } : order
    );
    setOrders(updatedOrders);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <h1 className="text-2xl font-bold mb-6">Екран на служител</h1>
      <div className="bg-white shadow rounded-lg p-4">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr>
              <th className="border-b p-4 font-semibold text-gray-700">
                Заглавие
              </th>
              <th className="border-b p-4 font-semibold text-gray-700">
                Клиент
              </th>
              <th className="border-b p-4 font-semibold text-gray-700">
                Статус
              </th>
              <th className="border-b p-4 font-semibold text-gray-700">
                Действия
              </th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id} className="hover:bg-gray-50">
                <td className="border-b p-4">{order.title}</td>
                <td className="border-b p-4">{order.customer}</td>
                <td className="border-b p-4">{order.status}</td>
                <td className="border-b p-4">
                  <div className="flex space-x-2">
                    <button
                      onClick={() => updateOrderStatus(order.id, "Отхвърлен")}
                      className="bg-red-500 text-white p-2 rounded-lg hover:bg-red-600"
                    >
                      Отхвърли
                    </button>
                    <button
                      onClick={() => updateOrderStatus(order.id, "В процес")}
                      className={`p-2 text-white rounded-lg ${
                        order.status === "В процес" ||
                        order.status === "Завършен"
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-blue-500 hover:bg-blue-600"
                      }`}
                      disabled={
                        order.status === "В процес" ||
                        order.status === "Завършен"
                      }
                    >
                      Започни
                    </button>
                    <button
                      onClick={() => updateOrderStatus(order.id, "Завършен")}
                      className={`p-2 text-white rounded-lg ${
                        order.status === "Завършен"
                          ? "bg-gray-300 cursor-not-allowed"
                          : "bg-green-500 hover:bg-green-600"
                      }`}
                      disabled={order.status === "Завършен"}
                    >
                      Завърши
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
