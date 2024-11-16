import { useState } from 'react'
import {Routes, Route} from 'react-router-dom'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

import RegisterPage from './pages/RegisterPage'

function App() {
  const [count, setCount] = useState(0)

  return (
    <Routes>
      <Route path='/' element={<RegisterPage/>}/>
    </Routes>
  )
}

export default App
