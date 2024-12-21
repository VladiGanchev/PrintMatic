import HeaderUser from "../components/headerUser";
import { useState, useEffect } from "react";
import { getUserPayments } from "../services/paymentService";

const PAYMENT_LABELS = {
  STRIPE: "карта",
  BALANCE: "баланс"
};

export default function UserNotifications() {
  const [payments, setPayments] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  // const [info, setInfo] = useState("")
  const pageSize = 5;

  const fetchPayments = async (page) => {
    try {
      const response = await getUserPayments(page, pageSize)
      setPayments(response.content)
      setTotalPages(response.totalPages)
    } catch (error) {
      console.log(error)
    }
  }

  useEffect(() => {
    fetchPayments(currentPage)
  }, [currentPage])

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage)
  }

  const formatDate = (dateString) => {
    const options = {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return new Date(dateString).toLocaleString('bg-BG', options);
  }

  const formatPrice = (number) => {
    return new Intl.NumberFormat('bg-BG', {
      style: 'currency',
      currency: 'BGN'
    }).format(number);
  }

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12 overflow-scroll">
        <p className="text-5xl font-bold">Известия за извършени плащания</p>
        <div className="space-y-4 mt-8">
          {payments.length === 0 ? (<p className="text-lg text-gray-500">Все още нямате извършени плащания</p>)
            :
            (payments.map((payment) => (
              <div
                key={payment.id}
                className="w-1/2 min-h-16 bg-gray-200 flex flex-col p-4 rounded-lg"
              >
                <p className="text-lg font-bold">{formatDate(payment.paidAt)}</p>
                <p className="text-md">Извършихте плащане на стойност {formatPrice(payment.amount)} чрез {PAYMENT_LABELS[payment.paymentType]}.</p>
              </div>
            )))}
        </div>
        {totalPages > 1 && (
          <div className="flex justify-center mt-8 gap-2 pb-8">
            <button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className="px-4 py-2 bg-gray-200 rounded-lg disabled:opacity-50 hover:bg-gray-300 transition-colors"
            >
              Предишна
            </button>

            {[...Array(totalPages)].map((_, index) => (
              <button
                key={index}
                onClick={() => handlePageChange(index)}
                className={`px-4 py-2 rounded-lg transition-colors ${currentPage === index
                  ? 'bg-primary text-white'
                  : 'bg-gray-200 hover:bg-gray-300'
                  }`}
              >
                {index + 1}
              </button>
            ))}

            <button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
              className="px-4 py-2 bg-gray-200 rounded-lg disabled:opacity-50 hover:bg-gray-300 transition-colors"
            >
              Следваща
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
