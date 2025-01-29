import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { payOrderFromBalance, createOrderPaymentSession } from '../services/paymentService';

const PAPER_TYPE_LABELS = {
  REGULAR_MATE: "Стандартна",
  GLOSSY: "Гланцирана",
  BRIGHT_WHITE: "Ярко бяла",
  PHOTO: "Фотохартия",
  HEAVYWEIGHT: "Тежка (плътна) хартия"
};

const DEADLINE_LABELS = {
  ONE_HOUR: "До час",
  ONE_DAY: "До ден",
  THREE_DAYS: "До три дни",
  ONE_WEEK: "До седмица"
};

export default function userPayment() {
  const navigate = useNavigate();
  const location = useLocation()

  const [paymentMethod, setPaymentMethod] = useState('stripe');
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState(null);

  const orderData = location.state?.orderData
  const price = location.state?.price
  const formula = location.state?.formula
  const orderId  = location.state?.orderId

  const formatPrice = (number) => {
    return new Intl.NumberFormat('bg-BG', {
      style: 'currency',
      currency: 'BGN'
    }).format(number);
  }

  useEffect(() => {
    console.log('Location State:', location.state);
  }, [location.state]);

  const handlePayment = async () => {
    setIsProcessing(true);
    setError(null);

    try {
      if (paymentMethod === 'stripe') {
        const sessionForStripePayment = await createOrderPaymentSession(orderId)
        const stripePaymentUrl = sessionForStripePayment?.stripePaymentURL
        localStorage.setItem('session', sessionForStripePayment.sessionId)

        if(stripePaymentUrl){
          window.location.href = stripePaymentUrl
        }else{
          alert("Error: Stripe payment URL not found.")
        }
        console.log('Processing Stripe payment...');
      } else {
        await payOrderFromBalance(orderId)
        navigate('/userHistory')
        console.log('Processing account balance payment...');
      }
    } catch (err) {
      setError('Възникна грешка при обработката на плащането');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
      <div className="w-full flex flex-col py-8">
        <div className="max-w-4xl mx-auto w-full">
          <h1 className="flex justify-center text-primary text-5xl font-bold mb-8">Плащане на поръчката</h1>

          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-2xl font-semibold mb-4">Детайли на поръчката</h2>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-gray-600">Заглавие:</p>
                <p className="font-medium">{orderData?.title}</p>
              </div>
              <div>
                <p className="text-gray-600">Брой копия:</p>
                <p className="font-medium">{orderData?.copies}</p>
              </div>
              <div>
                <p className="text-gray-600">Размер:</p>
                <p className="font-medium">{orderData?.pageSize}</p>
              </div>
              <div>
                <p className="text-gray-600">Тип хартия:</p>
                <p className="font-medium">{PAPER_TYPE_LABELS[orderData?.paperType]}</p>
              </div>
              <div>
                <p className="text-gray-600">Принтиране:</p>
                <p className="font-medium">
                  {orderData?.doubleSided ? 'Двустранно' : 'Едностранно'}
                </p>
              </div>
              <div>
                <p className="text-gray-600">Срок:</p>
                <p className="font-medium">{DEADLINE_LABELS[orderData?.deadline]}</p>
              </div>
            </div>

            <div className="mt-6 pt-6 border-t">
              <div className="flex justify-between items-center">
                <span className="text-s font-bold">Формула за пресмятане на цената:</span>
                <span className="text-xs font-semibold text-primary whitespace-pre-wrap">
                  {formula}
                </span>
              </div>
            </div>

            <div className="mt-6 pt-6 border-t">
              <div className="flex justify-between items-center">
                <span className="text-xl font-semibold">Обща сума:</span>
                <span className="text-2xl font-bold text-primary">
                  {formatPrice(price)}
                </span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-2xl font-semibold mb-4">Избор на плащане</h2>
            <div className="space-y-4">
              <label className="flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50">
                <input
                    type="radio"
                  name="payment"
                  value="stripe"
                  checked={paymentMethod === 'stripe'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="form-radio h-5 w-5 text-primary"
                />
                <span className="ml-3">Плащане с карта</span>
              </label>
              
              <label className="flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50">
                <input
                  type="radio"
                  name="payment"
                  value="balance"
                  checked={paymentMethod === 'balance'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="form-radio h-5 w-5 text-primary"
                />
                <span className="ml-3">Плащане с наличност</span>
              </label>
            </div>
          </div>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          <div className="flex justify-center">
            <button
              className="bg-primary text-white font-semibold p-2 rounded-lg hover:bg-primary-700 w-48 disabled:opacity-50"
              onClick={handlePayment}
              disabled={isProcessing}
            >
              {isProcessing ? "Обработка..." : "Плати поръчката"}
            </button>
          </div>
        </div>
      </div>
  );
}