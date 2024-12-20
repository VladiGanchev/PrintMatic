import React from "react";
import { useState } from "react";

const PaymentModal = ({ isOpen, onClose, onSubmit }) => {
  if (!isOpen) return null; 

  const [amount, setAmount] = useState("");

  const handleInputChange = (e) => {
    const value = e.target.value;

    if (/^\d*\.?\d{0,2}$/.test(value)) {
      setAmount(value);
    }
  };

  const handleSubmit = () => {
    if (!amount) {
      alert("Please enter a valid amount.");
      return;
    }
    onSubmit(amount);
    setAmount(""); // Clear the input field
    onClose(); // Close the modal
  };


  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white rounded-lg p-6 w-96">
        <h2 className="text-md font-bold mb-4">Въведете размера на депозита</h2>
        <input
          type="text"
          value={amount}
          onChange={handleInputChange}
          placeholder="Средства"
          className="w-full px-4 py-2 border rounded-lg mb-4"
        />
        <div className="flex justify-end space-x-3">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300"
          >
            Close
          </button>
          <button
            onClick={handleSubmit}
            className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );
};

export default PaymentModal;

