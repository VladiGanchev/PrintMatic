import api from './api'

export const payOrderFromBalance = async(orderId) => {
    try{
        const response = await api.post(`/api/payment/payOrderFromBalance/${orderId}`)
        return response.data;
    }catch(error){
        console.error("ERROR payment from balance: ", error)
        throw error.response?.data || 'Payment from balance cant be done'
    }
}

export const createOrderPaymentSession = async(orderId) =>{
    try{
        const response = await api.post(`/api/payment/createOrderSession/${orderId}`)
        return response.data;
    }catch(error){
        console.error("ERROR creating order session: ", error)
        throw error.response?.data || 'Creating order session failed'
    }
}

export const orderPaymentSuccess = async(orderPaymentDetails) =>{
    try{
        const response = await api.post("/api/payment/orderSuccess",orderPaymentDetails)
        return response.data;
    }catch(error){
        console.error("ERROR payment wasnt successful: ", error)
        throw error.response?.data || 'Payment whit stripe failed'
    }
}

export const getUserPayments = async(page, size) =>{
    try{
        const response = await api.get(`api/payment/getPayments?page=${page}&size=${size}`)
        return response.data;
    }catch(error){
        console.error("ERROR getting payments for the user: ", error)
        throw error.response?.data || 'Getting payment failed'
    }
}