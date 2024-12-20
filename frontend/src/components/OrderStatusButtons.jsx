import React from 'react';
import { Tooltip } from 'react-tooltip'
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
    await onStatusChange(order.id, 'REJECTED');
    await onRefresh();
  };

  return (
    <>
      <td className="border-y">
        <button
          data-tooltip-id='cancel-tooltip'
          data-tooltip-content='Reject order'
          onClick={(e) => handleCancelClick(e)}
          className="flex items-center justify-center rounded-full bg-red-500 w-8 h-8 hover:bg-red-700"
        >
          <TbCancel className="text-white w-6 h-6" />
        </button>
        <Tooltip
          id='cancel-tooltip'
          style={{
            fontWeight: 'bold',
            backgroundColor: 'rgb(209 213 219)', 
            color: 'black' 
          }}
           />
      </td>
      <td className="border-y">
        <button
          data-tooltip-id='start-order-tooltip'
          data-tooltip-content='Start order'
          onClick={(e) => handleProgressClick(e)}
          disabled={order.status !== 'PENDING'}
          className={`flex items-center justify-center rounded-full ${order.status === 'PENDING' ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-gray-300 cursor-not-allowed'
            } w-8 h-8`}
        >
          <TbProgress className="text-white w-6 h-6" />
        </button>
        <Tooltip
          id='start-order-tooltip'
          style={{
            fontWeight: 'bold',
            backgroundColor: 'rgb(209 213 219)', 
            color: 'black' 
          }} />
      </td>
      <td className="border-y">
        <button
          data-tooltip-id='complete-tooltip'
          data-tooltip-content='Complete order'
          onClick={(e) => handleCompleteClick(e)}
          disabled={order.status !== 'IN_PROGRESS'}
          className={`flex items-center justify-center rounded-full ${order.status === 'IN_PROGRESS' ? 'bg-green-500 hover:bg-green-700' : 'bg-gray-300 cursor-not-allowed'
            } w-8 h-8`}
        >
          <IoCheckmarkOutline className="text-white w-6 h-6" />
        </button>
        <Tooltip
          id='complete-tooltip' 
          style={{
            fontWeight: 'bold',
            backgroundColor: 'rgb(209 213 219)', 
            color: 'black' 
          }}/>
      </td>
    </>
  );
};

export default OrderStatusButtons;