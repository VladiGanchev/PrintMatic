import api, { setAuthToken } from "./api";

export const loginUser = async (credentials) => {
    try {
        const response = await api.post("/api/user/login", credentials);
        console.log('RESPONSE ' + response.data.token)
        const { token } = response.data;
        const { roles } = response.data;
        setAuthToken(token);
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('roles', roles);
        return response.data;

    } catch (error) {
        console.error("ERROR login: ", error);
        throw error.response?.data || 'Login failed';
    }
}

export const registerUser = async (userData) => {
    try {
        const response = await api.post("/api/user/register", userData);
        return response.data;

    } catch (error) {
        console.error("ERROR register: ", error);
        throw error.response?.data || 'Register failed';
    }

}