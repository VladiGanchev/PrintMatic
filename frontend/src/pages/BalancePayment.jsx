import { useEffect, useState, useRef } from 'react'
import { depositBalanceSuccess } from '../services/userService'
import TankYouForPaynment from '../components/TankYouForPaynment'

const BalancePayment = () => {
    const[messageResult, setMessageResult] = useState("")

    const handlePayment = async()=>{
        console.log("in initialize page")
        try {
            const sessionId = localStorage.getItem("session")
            const result = await depositBalanceSuccess(sessionId);
            setMessageResult(result.message)
        } catch (error) {
            console.error("Error processing payment:", error);
            // alert("An error occurred while processing your payment.");
        }
        finally{
            localStorage.removeItem("session")
        }
    }
    const isMounted = useRef(false);

    useEffect(() => {
        if (!isMounted.current) {
            console.log('First mount only');
            handlePayment();
            isMounted.current = true;
        }
    }, []);
    

    return (
        <div>
            <TankYouForPaynment title={"Плащането е извършено успешно"} text={"Скоро ще бъдете пренасочени към началната страница или щракнете тук, за да се върнете към началната страница"} />
        </div>
    )
}

export default BalancePayment
