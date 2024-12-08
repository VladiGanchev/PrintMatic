import HeaderUser from "../components/headerUser";

export default function UserNotifications() {
  const notifications = [
    {
      id: 1,
      date: "08.12.2024 г.",
      message:
        "Вашата заявка за принтиране е изпринтирана и готова за взимане от нашия център на ул. Стамболов.",
    },
    {
      id: 2,
      date: "07.12.2024 г.",
      message:
        "Вашият документ е успешно качен и очаква потвърждение за печат.",
    },
    {
      id: 3,
      date: "06.12.2024 г.",
      message:
        "Вашата поръчка е маркирана като 'В процес'. Моля, очаквайте известие за завършване.",
    },
  ];

  return (
    <div className="w-screen h-screen flex flex-row">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12">
        <p className="text-5xl font-bold">Известия</p>
        <div className="space-y-4 mt-8">
          {notifications.map((notification) => (
            <div
              key={notification.id}
              className="w-1/2 min-h-16 bg-gray-200 flex flex-col p-4 rounded-lg"
            >
              <p className="text-lg font-bold">{notification.date}</p>
              <p className="text-md">{notification.message}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
