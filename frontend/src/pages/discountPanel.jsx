import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { getAllDiscounts } from "../services/discountService.js";

const DiscountsPanel = () => {
    const navigate = useNavigate();
    const { hasAnyRole } = useAuth();

    const [discounts, setDiscounts] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    if (!hasAnyRole(["ADMIN"])) {
        navigate("/unauthorize");
    }

    const fetchDiscounts = async () => {
        setIsLoading(true);
        try {
            const data = await getAllDiscounts();
            let sortedDiscounts = data.sort((a, b) => a.priority - b.priority);
            setDiscounts(sortedDiscounts);
            console.log(sortedDiscounts);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchDiscounts();
    }, []);

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
            <h1 className="text-2xl font-bold mb-6">Отстъпки и Надценки</h1>
            <div className="bg-white shadow rounded-lg p-4">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                        <tr>
                            <th className="border-b p-4 font-semibold text-gray-700">Име</th>
                            <th className="border-b p-4 font-semibold text-gray-700">Описание</th>
                            <th className="border-b p-4 font-semibold text-gray-700">Тип</th>
                            <th className="border-b p-4 font-semibold text-gray-700">Приоритет</th>
                        </tr>
                        </thead>
                        <tbody>
                        {discounts.length === 0 ? (
                            <tr>
                                <td colSpan="5" className="text-center p-4 text-gray-500">
                                    Няма налични отстъпки
                                </td>
                            </tr>
                        ) : (
                            discounts.map((discount) => (
                                <tr key={discount.id}>
                                    <td className="border-b p-4">{discount.name}</td>
                                    <td className="border-b p-4">{discount.description}</td>
                                    <td className="border-b p-4">{discount.percentage ? "Процент" : "Стойност"}</td>
                                    <td className="border-b p-4">{discount.priority}</td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default DiscountsPanel;
