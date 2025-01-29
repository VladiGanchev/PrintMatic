import api from "./api";

export const updateService = async (service) => {
    try {
        const response = await api.put("/api/services/" + service.id, service);
        return response.data;
    } catch (error) {
        console.error("Update of service failed - ", error)
        return "Update of service failed - " + error
    }
}

export const getAllServices = async () => {
    try {
        const response = await api.get("/api/services/");
        return response.data;
    } catch (error) {
        console.error("Fetch of all services failed - ", error)
        return "Fetch of all services failed - " + error
    }
}