import api from './api'

export const searchByEmail = async(query) => {
    try{
        const response = await api.get(`/api/admin/search/email?query=${query}`)
        return response.data
    }catch (error) {
        console.error("ERROR can`t search the user by the email: ", error)
        throw error.response?.data || 'Searching the user by email failed';
    }
}

export const grantRoleToUser = async(email, role) =>{
    try{
        const response = await api.post(`/api/admin/grantRole?email=${email}&role=${role}`)
        return response.data
    }catch (error) {
        console.error("ERROR can`t grant role to the user: ", error)
        throw error.response?.data || 'Granting role to user failed';
    }
}


export const removeRoleToUser = async(email, role) =>{
    try{
        const response = await api.delete(`/api/admin/removeRole?email=${email}&role=${role}`)
        return response.data
    }catch (error) {
        console.error("ERROR can`t remove role to the user: ", error)
        throw error.response?.data || 'Removing role to user failed';
    }
}