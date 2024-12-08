import { FaCheckCircle } from "react-icons/fa";
import HeaderUser from "../components/headerUser";

export default function UserHistory() {
  const pastRequests = [
    {
      id: 1,
      fileName: "Документ1.docx",
      status: "Готова",
      date: "08.12.2024 г.",
      icon: "/images/word.png",
    },
    {
      id: 2,
      fileName: "Документ2.doc",
      status: "Готова",
      date: "07.12.2024 г.",
      icon: "/images/word.png",
    },
    {
      id: 3,
      fileName: "Форма.pdf",
      status: "Готова",
      date: "06.12.2024 г.",
      icon: "/images/pdf.png",
    },
  ];

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen overflow-scroll flex flex-col p-12">
        <p className="text-5xl font-bold">Минали заявки</p>
        <div className="flex flex-row w-2/3 flex-wrap mt-12">
          {pastRequests.map((request) => (
            <a
              key={request.id}
              className="w-1/4 min-h-16 bg-gray-200 flex flex-col p-4 rounded-lg m-4 hover:scale-[1.02] duration-300 transition-all"
              href="/userHistoryDocument"
            >
              <img src={request.icon} alt={request.fileName} className="w-12" />
              <p className="text-md font-bold">{request.fileName}</p>
              <div className="flex flex-row gap-1 items-center">
                <p className="text-md">Статус: {request.status}</p>
                <FaCheckCircle className="text-green-500 text-md" />
              </div>
              <p className="text-md">Дата: {request.date}</p>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
