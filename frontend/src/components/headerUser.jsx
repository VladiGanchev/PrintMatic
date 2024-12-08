import {
  FaBell,
  FaHistory,
  FaHome,
  FaSignOutAlt,
  FaUser,
} from "react-icons/fa";

export default function HeaderUser() {
  return (
    <div className="h-screen bg-primary w-48 flex flex-col justify-between p-4">
      <div className="flex flex-col gap-4">
        <p className="text-white font-bold text-2xl">PrintMatic</p>
        <div className="flex flex-row gap-2 items-center mt-16">
          <FaHome className="text-white" />
          <a className="text-white hover:underline" href="/userHome">
            Нова заявка
          </a>
        </div>
        <div className="flex flex-row gap-2 items-center">
          <FaBell className="text-white" />
          <a className="text-white hover:underline" href="/userNotifications">
            Известия
          </a>
        </div>
        <div className="flex flex-row gap-2 items-center">
          <FaHistory className="text-white" />
          <a className="text-white hover:underline" href="/userHistory">
            Минали заявки
          </a>
        </div>
        <div className="flex flex-row gap-2 items-center">
          <FaUser className="text-white" />
          <a className="text-white hover:underline" href="/userProfile">
            Профил
          </a>
        </div>
      </div>
      <div className="flex flex-row items-center gap-4">
        <FaSignOutAlt className="text-white" />
        <a className="text-white" href="/">
          Изход
        </a>
      </div>
    </div>
  );
}
