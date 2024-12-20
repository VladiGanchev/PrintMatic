import React, { useState, useEffect } from "react";
import { ordersPendingOrInProgress, downloadFile, updateOrderStatus } from "../services/orderService";
import { IoChevronDown, IoChevronUp, IoDownload, IoCheckmarkOutline } from "react-icons/io5";
import { TbCancel, TbProgress } from "react-icons/tb";
import OrderStatusButtons from "../components/OrderStatusButtons";

export default function EmployeeScreen() {
  const [orders, setOrders] = useState([]);
  const [sortBy, setSortBy] = useState("DEADLINE");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [expandedRows, setExpandedRows] = useState(new Set());
  const pageSize = 10;

  const fetchOrders = async (page) => {
    try {
      const response = await ordersPendingOrInProgress(sortBy, page, pageSize);
      setOrders(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      setError(error.message);
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFileDownload = async (blobName, e) => {
    e.stopPropagation()
    try {
      const response = await downloadFile(blobName)
      const fileUrl = response.message
      window.open(fileUrl, '_blank')
      console.log(fileUrl)
    } catch (error) {
      console.error('Error downloading file:', error);
    }
  }

  const handleOrderStatusChange = async (orderId, orderStatus) => {
    try {
      await updateOrderStatus(orderId, orderStatus)
    } catch (error) {
      console.error('Error changing order status:', error);
    }
  }

  useEffect(() => {
    fetchOrders(currentPage);
  }, [currentPage]);

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('bg-BG');
  };

  const toggleRowExpansion = (orderId) => {
    setExpandedRows((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(orderId)) {
        newSet.delete(orderId);
      } else {
        newSet.add(orderId);
      }
      return newSet;
    });
  };

  const translatePaperType = (paperType) => {
    const types = {
      REGULAR_MATE: 'Стандартна',
      GLOSSY: 'Гланцирана',
      BRIGHT_WHITE: 'Ярко бяла',
      PHOTO: 'Фотохартия',
      HEAVYWEIGHT: 'Тежка (плътна) хартия'
    };
    return types[paperType] || paperType;
  };

  const translateStatus = (status) => {
    const statuses = {
      PENDING: 'Изчакващ',
      IN_PROGRESS: 'В процес'
    };
    return statuses[status] || status;
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
      <h1 className="text-2xl font-bold mb-6">Екран на служител</h1>
      <div className="bg-white shadow rounded-lg p-4">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <caption className="text-md font-bold">Поръчки за изпълнение</caption>
            <thead>
              <tr>
                <th className="border-b p-4 font-semibold text-gray-700"></th>
                <th className="border-b p-4 font-semibold text-gray-700">Заглавие</th>
                <th className="border-b p-4 font-semibold text-gray-700">Файл</th>
                <th className="border-b p-4 font-semibold text-gray-700">Статус</th>
                <th className="border-b p-4 font-semibold text-gray-700">Създадена</th>
                <th className="border-b p-4 font-semibold text-gray-700">Краен срок</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan="9" className="text-center p-4 text-gray-500">
                    Все още няма поръчки
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <React.Fragment key={order.id}>
                    <tr className="hover:bg-gray-50 cursor-pointer" onClick={() => toggleRowExpansion(order.id)}>
                      <td className="border-b p-4">
                        {expandedRows.has(order.id) ? (
                          <IoChevronUp className="w-4 h-4" />
                        ) : (
                          <IoChevronDown className="w-4 h-4" />
                        )}
                      </td>
                      <td className="border-b p-4">{order.title}</td>
                      <td className="border-b p-4">
                        <a
                          className="flex items-center text-blue-600 hover:text-blue-800"
                          onClick={(e) => handleFileDownload(order.fileUrl, e)}
                        >
                          <IoDownload className="w-4 h-4 mr-1" />
                          Изтегли
                        </a>
                      </td>
                      <td className="border-b p-4">{translateStatus(order.status)}</td>
                      <td className="border-b p-4">{formatDate(order.createdAt)}</td>
                      <td className="border-b p-4">{formatDate(order.deadline)}</td>
                      <OrderStatusButtons
                        order={order}
                        onStatusChange={handleOrderStatusChange}
                        onRefresh={() => fetchOrders(currentPage)}
                      />
                    </tr>
                    {expandedRows.has(order.id) && (
                      <tr>
                        <td colSpan="9" className="border-b bg-gray-50 p-4">
                          <div className="grid grid-cols-2 gap-4">
                            <div>
                              <p className="font-semibold mb-2">Детайли на поръчката:</p>
                              <ul className="space-y-2">
                                <li>Брой копия: {order.copies}</li>
                                <li>Двустранно: {order.doubleSided ? 'Да' : 'Не'}</li>
                                <li>Размер на хартия: {order.pageSize}</li>
                                <li>Тип хартия: {translatePaperType(order.paperType)}</li>
                              </ul>
                            </div>
                            <div>
                              <p className="font-semibold mb-2">Допълнителна информация:</p>
                              <p className="whitespace-pre-wrap">{order.additionalInfo || 'Няма'}</p>
                            </div>
                          </div>
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center mt-4 gap-2">
            <button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className={`px-4 py-2 rounded ${currentPage === 0
                ? 'bg-gray-200 cursor-not-allowed'
                : 'bg-blue-500 text-white hover:bg-blue-600'
                }`}
            >
              Предишна
            </button>
            <div className="flex items-center gap-2">
              {[...Array(totalPages)].map((_, index) => (
                <button
                  key={index}
                  onClick={() => handlePageChange(index)}
                  className={`px-4 py-2 rounded ${currentPage === index
                    ? 'bg-blue-500 text-white'
                    : 'bg-gray-200 hover:bg-gray-300'
                    }`}
                >
                  {index + 1}
                </button>
              ))}
            </div>
            <button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
              className={`px-4 py-2 rounded ${currentPage === totalPages - 1
                ? 'bg-gray-200 cursor-not-allowed'
                : 'bg-blue-500 text-white hover:bg-blue-600'
                }`}
            >
              Следваща
            </button>
          </div>
        )}
      </div>
    </div>
  );
}