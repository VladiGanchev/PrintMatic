import { useAuth } from "../context/AuthContext";

import {
  FaBell,
  FaHistory,
  FaHome,
  FaSignOutAlt,
  FaUser,
  FaUserCog,
  FaClipboardList
} from "react-icons/fa";


export default function HeaderUser() {
  const { hasRole, hasAnyRole } = useAuth();

  const handleClick = () => {
    localStorage.clear()
  }

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
        <div className="flex flex-col gap-2 mt-10">
          {hasAnyRole(['EMPLOYEE', 'ADMIN']) && (
            <div className="flex flex-row gap-2 items-center">
              <FaClipboardList className="text-white" />
              <a className="text-white hover:underline" href="/employeePanel">
                Служителски панел
              </a>
            </div>
          )
          }

          {hasRole('ADMIN') && (
            <div className="flex flex-row gap-2 items-center">
              <FaUserCog className="text-white w-4 h-4" />
              <a className="text-white hover:underline" href="/adminPanel">
                Администраторски панел
              </a>
            </div>
          )
          }

          {hasRole('ADMIN') && (
              <div className="flex flex-row gap-2 items-center">
                <FaUserCog className="text-white w-4 h-4" />
                <a className="text-white hover:underline" href="/admin/services">
                  Панел с услуги
                </a>
              </div>
          )
          }
           

          {hasRole('ADMIN') && (
              <div className="flex flex-row gap-2 items-center">
                <FaUserCog className="text-white w-4 h-4" />
                <a className="text-white hover:underline" href="/admin/discounts">
                  Панел с намаления
                </a>
              </div>
          )
}

        </div>

      </div>
      <div className="flex flex-row items-center gap-4" onClick={handleClick}>
        <FaSignOutAlt className="text-white" />
        <a className="text-white" href="/">
          Изход
        </a>
      </div>
    </div>
  );
}
