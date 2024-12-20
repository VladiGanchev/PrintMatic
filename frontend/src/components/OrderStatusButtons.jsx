import React from 'react';
import { TbCancel, TbProgress } from "react-icons/tb";
import { IoCheckmarkOutline } from "react-icons/io5";

const OrderStatusButtons = ({ order, onStatusChange, onRefresh }) => {
  const handleProgressClick = async (e) => {
    e.stopPropagation()
    if (order.status === 'PENDING') {
      await onStatusChange(order.id, 'IN_PROGRESS');
      await onRefresh();
    }
  };

  const handleCompleteClick = async (e) => {
    e.stopPropagation()
    if (order.status === 'IN_PROGRESS') {
      await onStatusChange(order.id, 'COMPLETED');
      await onRefresh();
    }
  };

  const handleCancelClick = async (e) => {
    e.stopPropagation()
    await onStatusChange(order.id, 'CANCELED');
    await onRefresh();
  };

  return (
    <>
      <td className="border-y">
        <button 
          onClick={(e) => handleCancelClick(e)}
          className="flex items-center justify-center rounded-full bg-red-500 w-8 h-8"
        >
          <TbCancel className="text-white w-6 h-6" />
        </button>
      </td>
      <td className="border-y">
        <button 
          onClick={(e) => handleProgressClick(e)}
          disabled={order.status !== 'PENDING'}
          className={`flex items-center justify-center rounded-full ${
            order.status === 'PENDING' ? 'bg-yellow-500' : 'bg-gray-300 cursor-not-allowed'
          } w-8 h-8`}
        >
          <TbProgress className="text-white w-6 h-6" />
        </button>
      </td>
      <td className="border-y">
        <button 
          onClick={(e) => handleCompleteClick(e)}
          disabled={order.status !== 'IN_PROGRESS'}
          className={`flex items-center justify-center rounded-full ${
            order.status === 'IN_PROGRESS' ? 'bg-green-500' : 'bg-gray-300 cursor-not-allowed'
          } w-8 h-8`}
        >
          <IoCheckmarkOutline className="text-white w-6 h-6" />
        </button>
      </td>
    </>
  );
};

export default OrderStatusButtons;