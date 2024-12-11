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

export const createOrder = async (orderData) =>{
    try{
        const response = await api.post("/api/order/create", orderData)
        return response.data;
    }catch(error){
        console.error("ERROR creating order: ", error)
        throw error.response?.data || 'Order creation failed'
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

