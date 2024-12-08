export default function HeaderHome() {
  return (
    <div className="bg-primary w-screen h-16 flex flex-row justify-between items-center">
      <a className="text-white text-3xl font-bold pl-8" href="/home">
        PrintMatic
      </a>
      <div className="flex flex-row items-center pr-8 gap-8">
        <a className="text-white font-bold hover:underline" href="/home">
          Начало
        </a>
        <a
          className="text-white font-bold hover:underline"
          href="/home#services"
        >
          Услуги
        </a>
        <a className="text-white font-bold hover:underline" href="/">
          Вход
        </a>
      </div>
    </div>
  );
}
