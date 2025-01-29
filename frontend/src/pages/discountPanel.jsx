import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import {
    getAllDiscounts,
    updateDiscount,
    addDiscount,
    deactivateDiscount
} from "../services/discountService.js";

const DiscountPanel = () => {
    const navigate = useNavigate();
    const { hasAnyRole } = useAuth();
    
    const [discounts, setDiscounts] = useState([]);
    console.log(discounts)
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isAddingNew, setIsAddingNew] = useState(false);
    const [newDiscount, setNewDiscount] = useState({
        name: "",
        description: "",
        value: 0,
        isPercentage: true,
        minimumOrderValue: null,
        minimumPageCount: null,
        startDate: new Date().toISOString().split('T')[0],
        endDate: null,
        applicableUserRole: null,
        applicableDeadline: null,
        isStackable: true,
        isActive: true,
        priority: 1
    });

    //Ensure only authorized users can access
    useEffect(() => {
        if (!hasAnyRole(["ADMIN", "EMPLOYEE"])) {
            navigate("/unauthorize");
        } else {
            fetchDiscounts();
        }
    }, []);

    // Fetch all discounts
    const fetchDiscounts = async () => {
        setIsLoading(true);
        try {
            console.log("Fetching in Pannel...")
            const data = await getAllDiscounts();
            console.log(data);
           
         //let discountsData = data.discounts.((a, b) => {return a.id - b.id});
            setDiscounts(data.discounts);
        } catch (err) {
            console.log(err);
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };
    useEffect(() => {
        fetchDiscounts();
    }, []);



    // Handle discount update
    const handleUpdateDiscount = async (id) => {
        try {
            const discountToUpdate = discounts.find(d => d.id === id);
            const updatedDiscount = await updateDiscount(discountToUpdate);
            if (updatedDiscount) {
                alert("Отстъпката е успешно обновена!");
                await fetchDiscounts();
            }
        } catch (err) {
            alert("Грешка при обновяването: " + err.message);
        }
    };

    // Handle adding a new discount
    const handleAddDiscount = async () => {
        try {
            const createdDiscount = await addDiscount(newDiscount);
            if (createdDiscount) {
                setIsAddingNew(false);
                setNewDiscount({
                    name: "",
                    description: "",
                    value: 0,
                    isPercentage: true,
                    minimumOrderValue: null,
                    minimumPageCount: null,
                    startDate: new Date().toISOString().split('T')[0],
                    endDate: null,
                    applicableUserRole: null,
                    applicableDeadline: null,
                    isStackable: true,
                    isActive: true,
                    priority: 1
                });
                await fetchDiscounts();
                alert("Отстъпката е успешно добавена!");
            }
        } catch (err) {
            alert("Грешка при добавянето: " + err.message);
        }
    };

    // Handle discount deactivation
    const handleDeactivateDiscount = async (id) => {
        if (window.confirm("Сигурни ли сте, че искате да деактивирате тази отстъпка?")) {
            try {
                const deactivated = await deactivateDiscount(id);
                if (deactivated) {
                    await fetchDiscounts();
                    alert("Отстъпката е деактивирана успешно!");
                }
            } catch (err) {
                alert("Грешка при деактивирането: " + err.message);
            }
        }
    };
    
    if (isLoading) {
        return (
            <div className="w-full h-screen flex justify-center items-center">
                <p className="text-2xl">Зареждане...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="w-full h-screen flex justify-center items-center">
                <p className="text-2xl text-red-500">Грешка: {error}</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100 p-6">
            <h1 className="text-2xl font-bold mb-6">Отстъпки</h1>

            <div className="bg-white shadow rounded-lg p-4">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr>
                                <th className="border-b p-4 font-semibold text-gray-700">Име</th>
                                <th className="border-b p-4 font-semibold text-gray-700">Описание</th>
                                <th className="border-b p-4 font-semibold text-gray-700">Стойност</th>
                                <th className="border-b p-4 font-semibold text-gray-700">Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {discounts.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="text-center p-4 text-gray-500">
                                        Няма налични отстъпки
                                    </td>
                                </tr>
                            ) : (
                                
                                discounts.map((discount) => (
                                    <tr key={discount.id}>
                                        <td className="border-b p-4">{discount.name}</td>
                                        <td className="border-b p-4">{discount.description}</td>
                                        <td className="border-b p-4">{discount.isPercentage ? `${discount.value}%` : `${discount.value} лв.`}</td>
                                        <td className="border-b p-4 flex space-x-2">
                                            <button
                                                onClick={() => handleUpdateDiscount(discount.id)}
                                                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                            >
                                                Запази
                                            </button>
                                            <button
                                                onClick={() => handleDeactivateDiscount(discount.id)}
                                                className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                            >
                                                Деактивирай
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Add Discount Form */}
            {isAddingNew ? (
                <div className="bg-white shadow rounded-lg p-4 mt-6">
                    <h2 className="text-xl font-semibold mb-4">Добавяне на нова отстъпка</h2>
                    <input
                        type="text"
                        placeholder="Име"
                        value={newDiscount.name}
                        onChange={(e) => setNewDiscount({ ...newDiscount, name: e.target.value })}
                        className="border rounded px-2 py-1 w-full mb-2"
                    />
                    <textarea
                        placeholder="Описание"
                        value={newDiscount.description}
                        onChange={(e) => setNewDiscount({ ...newDiscount, description: e.target.value })}
                        className="border rounded px-2 py-1 w-full mb-2"
                    />
                    <input
                        type="number"
                        placeholder="Стойност"
                        value={newDiscount.value}
                        onChange={(e) => setNewDiscount({ ...newDiscount, value: parseFloat(e.target.value) })}
                        className="border rounded px-2 py-1 w-full mb-2"
                    />
                    <button
                        onClick={handleAddDiscount}
                        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                    >
                        Добави
                    </button>
                </div>
            ) : (
                <button
                    onClick={() => setIsAddingNew(true)}
                    className="mt-6 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                >
                    Добави нова отстъпка
                </button>
            )}
        </div>
    );
};

export default DiscountPanel;
