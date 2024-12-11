import api from './api'

export const getLoginUser = async() => {
    try{
        const response = await api.get("/api/user")
        return response.data;

    } catch (error) {
        console.error("ERROR finding the user: ", error)
        throw error.response?.data || 'User not found';
    }

}

export const updateUser = async(updateData) => {
    try{
        const response = await api.post("/api/user/update", updateData)
        return response.data;

    } catch (error) {
        console.error("ERROR updating the user: ", error)
        throw error.response?.data || 'User has`t been updated';
    }
}