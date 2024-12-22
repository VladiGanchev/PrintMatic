import React from "react";
import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext(null);

export const ROLES = {
    USER: 'USER',
    EMPLOYEE: 'EMPLOYEE',
    ADMIN: 'ADMIN'
};

export const AuthProvider = ({ children }) => {
    const [roles, setRoles] = useState([]);

    useEffect(() => {
        const storedRoles = localStorage.getItem('roles');
        if (storedRoles) {
            setRoles(storedRoles.split(','));
        }
    }, []);

    const hasRole = (requiredRole) => {
        return roles.includes(requiredRole);
    };

    const hasAnyRole = (requiredRoles) => {
        return requiredRoles.some(role => roles.includes(role));
    };

    const hasAllRoles = (requiredRoles) => {
        return requiredRoles.every(role => roles.includes(role));
    };

    return (
        <AuthContext.Provider value={{
            roles,
            hasRole,
            hasAnyRole,
            hasAllRoles
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const withRoleCheck = (WrappedComponent, requiredRoles) => {
    return function RoleCheckWrapper(props) {
        const { hasAnyRole } = useAuth();

        if (!hasAnyRole(requiredRoles)) {
            return null;
        }

        return <WrappedComponent {...props} />;
    };
};

export const ProtectedRoute = ({ children, roles }) => {
    const { hasAnyRole } = useAuth();

    if (!hasAnyRole(roles)) {
        return null;
    }

    return children;
};