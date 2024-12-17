import api from './api'

export const getLoginUser = async () => {
    try {
        const response = await api.get("/api/user")
        return response.data;

    } catch (error) {
        console.error("ERROR finding the user: ", error)
        throw error.response?.data || 'User not found';
    }

}

export const updateUser = async (updateData) => {
    try {
        const response = await api.post("/api/user/update", updateData)
        return response.data;

    } catch (error) {
        console.error("ERROR updating the user: ", error)
        throw error.response?.data || 'User has`t been updated';
    }
}

export const depositBalance = async (amount) => {
    try {
        const response = await api.get(`/api/payment/addToBalanceSession?amount=${amount}`)
        return response.data;

    } catch (error) {
        console.error("ERROR creating the stripe sessiont: ", error)
        throw error.response?.data || 'Can`t create the stripe session';
    }
}

export const depositBalanceSuccess = async (stripeId) => {
    console.log("in deposit ballance methos")
    try {
        const response = await api.post(`/api/payment/depositBalanceSuccess?stripeId=${stripeId}`)
        return response.data;

    } catch (error) {
        console.error("ERROR the payment hasn`t been successful: ", error)
        throw error.response?.data || 'Couldn`t pay';
    }
}