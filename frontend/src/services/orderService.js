import api from './api'

export const uploadFile = async (file, isGrayscale) =>{
    try{
        const formData = new FormData()
        formData.append("file", file)
        formData.append("grayscale", isGrayscale)

        const response = await api.post("/api/storage/upload", formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': `Bearer ${localStorage.getItem("jwtToken")}` 
            },
        })
   
        return response.data;

    } catch (error) {
        console.error("ERROR upload: ", error)
        throw error.response?.data || 'Upload failed';
    }
};

export const downloadFile = async (blobName) =>{
    try{
        const response = await api.get(`/api/storage/download?blobName=${blobName}`)
        return response.data;
    }catch(error){
        console.error("ERROR downloading the file: ", error)
        throw error.response?.data || 'Downloading file failed'
    }
}

export const createOrder = async (orderData) =>{
    try{
        const response = await api.post("/api/order/create", orderData)
        return response.data;
    }catch(error){
        console.error("ERROR creating order: ", error)
        throw error.response?.data || 'Order creation failed'
    }
}

export const updateOrderStatus = async(orderId, orderStatus) =>{
    try{
        const response = await api.post(`/api/order/updateOrderStatus/${orderId}?orderStatus=${orderStatus}`)
        return response.data
    }catch(error){
        console.error("ERROR changing the order status: ", error)
        throw error.response?.data || 'Order status change failed'
    }
}

export const getUserOrders = async (page = 0, size = 10) => {
    try{
        const response = await api.get(`/api/order/user?page=${page}&size=${size}`)
        return response.data;
    }catch(error){
        console.error("ERROR in displaying user orders: ", error)
        throw error.response?.data || 'Displaying user orders failed'
    }
}

export const ordersPendingOrInProgress = async(sortBy = DEADLINE, page = 0, size = 10) =>{
    try{
        const response = await api.get(`/api/order/getPendingOrInProgress?sortBy=${sortBy}&page=${page}&size=${size}`)
        return response.data;
    }catch(error){
        console.error("ERROR in displaying orders: ", error)
        throw error.response?.data || 'Displaying orders failed'
    }
}

export const getOrderById = async(orderId) =>{
    try{
        const response = await api.get(`/api/order/user/${orderId}`)
        return response.data;
    }catch(error){
        console.error("ERROR finding the order: ", error)
        throw error.response?.data || 'Order cant be found'
    }
}

