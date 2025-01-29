import api from "./api";


export const getAllDiscounts = async () => {
    try {
        console.log("Fetching...")
        const response = await api.get("/api/discounts/");
        console.log(response.status)
       return response.data;
    } catch (err) {
        console.error("Fetch of discount failed - ", err)

    } 
};

export const updateDiscount = async (discount) => {
    try {
        const response = await api.put(`/api/discounts/${discount.id}`, discount);
        return response.data;
    } catch (error) {
        console.error("Update of discount failed - ", error)
        throw error;
    }
};

export const addDiscount = async (discount) => {
    try {
        const response = await api.post("/api/discounts/", discount);
        return response.data;
    } catch (error) {
        console.error("Adding discount failed - ", error);
        throw error;
    }
};

export const deactivateDiscount = async (id) => {
    try {
        const response = await api.delete(`/api/discounts/${id}`);
        return response.data;
    } catch (error) {
        console.error("Deactivating discount failed - ", error);
        throw error;
    }
};

export const createDiscount = async (discountData) => {
    try {
        const response = await api.post("/api/discounts/", {
            name: discountData.name,
            description: discountData.description,
            value: discountData.value,
            isPercentage: discountData.isPercentage,
            minimumOrderValue: discountData.minimumOrderValue || null,
            minimumPageCount: discountData.minimumPageCount || null,
            startDate: discountData.startDate,
            endDate: discountData.endDate || null,
            applicableUserRole: discountData.applicableUserRole || null,
            applicableDeadline: discountData.applicableDeadline || null,
            isStackable: discountData.isStackable,
            isActive: true,
            priority: discountData.priority
        });
        return response.data;
    } catch (error) {
        console.error("Creating discount failed - ", error);
        throw error;
    }
};