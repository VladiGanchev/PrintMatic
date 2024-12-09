import HeaderUser from "../components/headerUser";

export default function UserPayment() {
  return (
    <div className="w-screen h-screen flex flex-row">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12">
        <p className="text-5xl font-bold">Плащане</p>
        <p>Екран за плащане</p>
      </div>
    </div>
  );
}
