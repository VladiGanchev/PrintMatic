import Footer from "../components/footer";
import HeaderHome from "../components/headerHome";
import { useNavigate } from "react-router-dom";

export default function Home() {
  return (
    <div className="w-screen min-h-screen flex flex-col bg-white hover:cursor-default">
      <HeaderHome />
      <div
        className="w-full h-96 bg-cover flex justify-center items-center bg-blend-multiply"
        style={{
          backgroundImage: "url(/images/home.jpg)",
          backgroundColor: "rgba(0,0,0,0.5)",
        }}
      >
        <p className="text-5xl text-white font-bold">PrintMatic</p>
      </div>
      <div className="flex flex-row justify-between w-full px-24 mt-12">
        <div className="w-1/2 flex flex-col gap-4">
          <h1 className="text-3xl font-bold">За нас</h1>
          <p className="w-full text-md">
            Дигитализирайте Вашето принт студио PrintMatic - платформа за
            управление на всички Ваши нужди от печат. Лесно регистрирайте и
            персонализирайте поръчките Ви с опции като копия, цвят и вид хартия,
            като същевременно проследявате напредъка в реално време. Останете
            уведомени на всяка стъпка и изберете гъвкави методи за плащане,
            включително PayPal или предварително зареден баланс. Операторите
            могат ефективно да управляват активните поръчки чрез интуитивно
            табло, докато администраторите се радват на пълен контрол с
            усъвършенствани инструменти за управление на потребителите и
            плащанията. Независимо дали сте клиент, оператор или администратор,
            PrintMatic опростява и рационализира целия процес, осигурявайки
            удобство и прецизност всеки път.
          </p>
        </div>
        <img
          className="w-1/3 rounded-lg drop-shadow-lg shadow-black"
          src="/images/home2.jpg"
          alt="home2"
        />
      </div>
      <div id="services" className="flex flex-col gap-4 px-24 my-12">
        <h1 className="text-3xl font-bold">Услуги</h1>
        <div className="flex flex-row justify-between gap-4">
          <div className="bg-gray-100 rounded-2xl w-96 min-h-96 flex flex-col gap-4 p-4 items-center hover:scale-[1.02] transition-all duration-300">
            <p className="text-md font-bold">За клиентите</p>
            <img
              src="/images/customer.jpg"
              alt="customer"
              className="w-3/4 rounded-xl"
            />
            <ul className="list-disc px-4 gap-2 flex flex-col">
              <li>
                Разгледайте и открийте нашите услуги на удобна за ползване
                начална страница.
              </li>
              <li>
                Безпроблемно се регистрирайте, влезте и управлявайте профила си.
              </li>
              <li>
                Персонализирайте поръчките си с опции за копия, цвят, вид хартия
                и др.
              </li>
              <li>
                Проследявайте поръчките си в реално време и получавайте
                незабавни известия за състоянието им.
              </li>
              <li>
                Удобни методи за плащане: PayPal или предварително зареден
                баланс.
              </li>
            </ul>
          </div>
          <div className="bg-gray-100 rounded-2xl w-96 min-h-96 flex flex-col gap-4 p-4 items-center hover:scale-[1.02] transition-all duration-300">
            <p className="text-md font-bold">За операторите</p>
            <img
              src="/images/employee.webp"
              alt="customer"
              className="w-3/4 rounded-xl"
            />
            <ul className="list-disc px-4 gap-2 flex flex-col">
              <li>
                Подробен преглед на поръчките: Преглед на всички активни поръчки
                с подробни спецификации като копия, опции за цвят и вид хартия.
              </li>
              <li>
                Актуализации на състоянието в реално време: Актуализирайте
                статусите на поръчките (напр. „В процес на изпълнение“,
                „Завършена“), за да информирате клиентите незабавно.
              </li>
              <li>
                Ефективно управление на задачите: Използвайте вградените филтри
                и функцията за търсене, за да намирате бързо и да подреждате по
                важност задачите.
              </li>
            </ul>
          </div>
          <div className="bg-gray-100 rounded-2xl w-96 min-h-96 flex flex-col gap-4 p-4 items-center hover:scale-[1.02] transition-all duration-300">
            <p className="text-md font-bold">За администраторите</p>
            <img
              src="/images/admin.jpg"
              alt="customer"
              className="w-3/4 rounded-xl"
            />
            <ul className="list-disc px-4 gap-2 flex flex-col">
              <li>
                Управление на потребителите: Създавайте, редактирайте и
                изтривайте потребителски акаунти без усилие, за да поддържате
                организирана система.
              </li>
              <li>
                Контрол на плащанията: Наблюдавайте и управлявайте всички
                транзакции, включително плащания и салда, за пълен финансов
                контрол.
              </li>
              <li>
                Надзор на поръчките: Получете достъп до подробен преглед на
                поръчките и техните статуси, за да осигурите безпроблемна
                работа.
              </li>
            </ul>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}
