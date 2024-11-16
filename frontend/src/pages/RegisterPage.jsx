import React, {useState} from 'react'
import { useNavigate } from 'react-router-dom'
import { registerUser, loginUser } from '../services/authService'

const RegisterPage = () => {
    let navigate = useNavigate()

    const [userData, setUserData] = useState({email: '', password: '', firstName: '', lastName: '', phoneNumber: ''})
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleRegister = async () =>{
        if(userData.password !== confirmPassword){
            setErrorMessage("Your password do not match the confirm password.")
            return;
        }
        
        try{
            const data = await registerUser(userData);
            console.log('User registered successfully:', data);

            const loginData = await loginUser({ email: userData.email, password: userData.password });
            console.log('User logged in successfully:', loginData);

        } catch (error) {
            console.error('Registration error: ', error)
            setErrorMessage(error.response ? error.response.data.message : 'Registration failed')
        }
    };


  return (
     <div className='flex justify-center items-center w-full'>
            <div className='flex flex-col justify-start items-start bg-primary text-white text-md py-4 px-6'>
                <p className='text-lg'>Register</p>
                <div className='flex text-s mb-6 space-x-2'>
                    <p className='text-gray-500'>Already have an account?</p>
                    <p className='cursor-pointer hover:text-gray-300'>Log in</p>
                </div>
                <div className='flex flex-row gap-x-8'>
                <div className='flex flex-col w-full items-center text-gray-500 space-y-4 pt-6'>
                    <input
                        type="text"
                        className='w-full p-2'
                        placeholder='Enter first name'
                        value={userData.firstName}
                        onChange={e => setUserData({...userData, firstName: e.target.value})}
                    />
                    <input
                        type="text"
                        className='w-full p-2'
                        placeholder='Enter last name'
                        value={userData.lastName}
                        onChange={e => setUserData({...userData, lastName: e.target.value}) }
                    />
                      <input
                        type="text"
                        className='w-full p-2'
                        placeholder='Enter phone'
                        value={userData.phoneNumber}
                        onChange={e => setUserData({...userData, phoneNumber: e.target.value}) }
                    />
                </div>
                <div className='flex flex-col  w-full items-center text-gray-500 space-y-4 pt-6'>
                    <input 
                        type="email" 
                        className='w-full bg-white p-2' 
                        placeholder='Email '
                        value={userData.email}
                        onChange={e => setUserData({...userData, email: e.target.value})}
                    />
                    <input 
                        type="password" 
                        className='w-full bg-white p-2' 
                        placeholder='Password'
                        value={userData.password}
                        onChange={e => setUserData({...userData, password: e.target.value})}
                    />
                    <input 
                        type="password" 
                        className='w-full bg-white p-2'
                        placeholder='Confirm password'
                        value={confirmPassword}
                        onChange={e => setConfirmPassword(e.target.value)}
                    />
                </div>
                </div>
                <div className='text-center w-full bg-white text-primary font-semibold my-10 cursor-pointer hover:bg-gray-300' 
                    onClick={handleRegister}>
                        Register
                </div>
                {errorMessage && <p className='text-red-500 text-sm'>{errorMessage}</p>}            
            </div>
            </div>
  )
}

export default RegisterPage
