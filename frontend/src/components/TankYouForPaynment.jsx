import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useEffect } from 'react'
import { FaCheck } from 'react-icons/fa6'

const TankYouForPaynment = ({ title, text }) => {
    let navigate = useNavigate()

    useEffect(() => {
        const timer = setTimeout(() => {
          navigate("/userHome");
        }, 6000);
    
        return () => clearTimeout(timer);
      }, []);

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white rounded-lg p-8 text-center w-1/3">
                <div className="flex justify-center items-center w-24 h-24 bg-green-100 rounded-full mx-auto mb-6">
                <FaCheck className='text-green-600 w-full h-1/2'/>
                </div>

                <h1 className="text-lg font-bold text-green-600 mb-2">Благодарим Ви!</h1>
                <p className="text-md text-gray-600 mb-4">{title}</p>
                <p className="text-gray-500 text-s mb-6">
                    {text}
                </p>

                <button
                    onClick={() => { navigate("/userHome") }}
                    className="px-6 py-3 bg-green-500 text-white rounded-lg font-medium hover:bg-green-600 transition">
                    Close
                </button>
            </div>
        </div>
    );
}

export default TankYouForPaynment
