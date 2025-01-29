import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {getAllServices, updateService} from "../services/servicesService.js";

const ServicesPanel = () => {
    const navigate = useNavigate();
    const { hasAnyRole } = useAuth();

    const [services, setServices] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    if (!hasAnyRole(["ADMIN"])) {
        navigate("/unauthorize");
    }

    const fetchServices = async () => {
        setIsLoading(true);
        try {
            const data = await getAllServices();
            let servicesData = data.servicePriceSet.sort((a, b) => {return a.id - b.id});
            setServices(servicesData);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    const getServiceById = (id) => {
        return services.find((service) => service.id === id);
    };

    const handlePriceChange = (id, newPrice) => {
        setServices((prevServices) =>
            prevServices.map((service) =>
                service.id === id ? { ...service, price: newPrice } : service
            )
        );
    };

    const savePriceChange = async (id) => {
        try {
            await updateService(getServiceById(id));
            alert("Цената е успешно обновена!");
        } catch (err) {
            alert(err);
        }
    };

    useEffect(() => {
        fetchServices();
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
            <h1 className="text-2xl font-bold mb-6">Услуги</h1>
            <div className="bg-white shadow rounded-lg p-4">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                        <tr>
                            <th className="border-b p-4 font-semibold text-gray-700">Име на услугата</th>
                            <th className="border-b p-4 font-semibold text-gray-700">Тип на цената</th>
                            <th className="border-b p-4 font-semibold text-gray-700">Стойност</th>
                            <th className="border-b p-4 font-semibold text-gray-700"></th>
                        </tr>
                        </thead>
                        <tbody>
                        {services.length === 0 ? (
                            <tr>
                                <td colSpan="4" className="text-center p-4 text-gray-500">
                                    Няма налични услуги
                                </td>
                            </tr>
                        ) : (
                            services.map((service) => (
                                <tr key={service.id}>
                                    <td className="border-b p-4">{service.service}</td>
                                    <td className="border-b p-4">{service.priceType === "VALUE" ? "Базова цена" : "Надценка"}</td>
                                    <td className="border-b p-4">
                                        <input
                                            type="number"
                                            value={service.price}
                                            onChange={(e) => handlePriceChange(service.id, parseFloat(e.target.value))}
                                            className="border rounded px-2 py-1 w-20"
                                        />
                                    </td>
                                    <td className="border-b p-4">
                                        <button
                                            onClick={() => savePriceChange(service.id)}
                                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                                        >
                                            Запази
                                        </button>
                                    </td>
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

export default ServicesPanel;
