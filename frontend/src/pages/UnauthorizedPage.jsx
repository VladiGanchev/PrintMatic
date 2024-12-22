import React from 'react'
import { useNavigate } from 'react-router-dom'
import { FaBan } from "react-icons/fa";

const UnauthorizedPage = () => {
    let navigate = useNavigate()
    return (
        <div className='flex flex-col items-center justify-center min-h-screen bg-gray-100'>
            <FaBan className='w-[50px] h-[50px] mb-6 text-red-600' />
            <h1 className='text-2xl font-bold text-red-600 mb-4'>Неоторизиран достъп</h1>
            <p className='text-gray-600 mb-4'>
                Нямате необходимите права за достъп до тази страница.
            </p>
            <button
                onClick={() => navigate('/userHome')}
                className='bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600'
            >
                Към началната страница
            </button>
        </div>
    )
}

export default UnauthorizedPage
