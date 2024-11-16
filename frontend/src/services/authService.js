import api, {setAuthToken} from "./api";

export const loginUser = async (credentials) =>{
    try{
        const response = await api.post("/user/login", credentials);
        
        const{token} = response.data.token;
        setAuthToken(token);
        localStorage.setItem('jwtToken', token);
        return response.data;
        
    }catch(error){
        console.error("ERROR login: ", error);
        throw error.response?.data || 'Login failed';
    }
}

export const registerUser = async (userData) =>{
    try {
            const response = await api.post("/user/register", userData);
            return response.data;

    } catch (error) {
        console.error("ERROR register: ", error);
        throw error.response?.data || 'Register failed';
    }

}