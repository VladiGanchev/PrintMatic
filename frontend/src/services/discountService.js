import api from "./api";

export const getAllDiscounts = async () => {
    try {
        const response = await api.get("/api/discounts/");
        return response.data;
    } catch (error) {
        console.error("Fetch of all discounts failed - ", error)
        return "Fetch of all discounts failed - " + error
    }
}