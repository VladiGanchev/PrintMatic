import { useNavigate } from "react-router-dom";

export default function HeaderHome() {
  let navigate = useNavigate()

  return (
    <div className="bg-primary w-screen h-16 flex flex-row justify-between items-center">
      <a className="text-white text-3xl font-bold pl-8" href="/home">
        PrintMatic
      </a>
      <div className="flex flex-row items-center pr-8 gap-8">
        <a className="text-white font-bold hover:underline">
          Начало
        </a>
        <a
          className="text-white font-bold hover:underline"
        >
          Услуги
        </a>
        <a className="text-white font-bold hover:underline" onClick={() => navigate("/login")}>
          Вход
        </a>
      </div>
    </div>
  );
}
