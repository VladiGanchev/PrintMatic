import { useState } from "react";
import { useDropzone } from "react-dropzone";
import HeaderUser from "../components/headerUser";

export default function UserHome() {
  const [files, setFiles] = useState([]);
  const [copies, setCopies] = useState(1);
  const [color, setColor] = useState("black-and-white");
  const [paperType, setPaperType] = useState("standard");

  const onDrop = (acceptedFiles) => {
    setFiles(acceptedFiles);
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      "application/pdf": [".pdf"],
      "application/msword": [".doc", ".docx"],
    },
  });

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12">
        <p className="text-5xl font-bold">Нова заявка</p>
        <div
          {...getRootProps()}
          className={`mt-8 w-1/2 h-40 flex flex-col hover:cursor-pointer items-center justify-center border-2 border-dashed rounded-lg p-4 ${
            isDragActive ? "border-blue-500 bg-blue-100" : "border-gray-400"
          }`}
        >
          <input {...getInputProps()} />
          {isDragActive ? (
            <p className="text-lg text-blue-500">Пуснете документа тук...</p>
          ) : (
            <p className="text-lg text-gray-600">Качете документа Ви</p>
          )}
        </div>
        <div className="mt-4">
          {files.length > 0 && (
            <ul className="list-disc pl-6">
              {files.map((file, index) => (
                <li key={index} className="text-gray-700">
                  {file.name} ({(file.size / 1024).toFixed(2)} KB)
                </li>
              ))}
            </ul>
          )}
        </div>

        <form className="mt-8 space-y-4">
          {/* Number of Copies */}
          <div>
            <label className="block text-gray-700 font-semibold">
              Брой копия:
            </label>
            <input
              type="number"
              min="1"
              value={copies}
              onChange={(e) => setCopies(e.target.value)}
              className="mt-1 block w-1/4 p-2 border rounded-md"
            />
          </div>

          {/* Color Options */}
          <div>
            <label className="block text-gray-700 font-semibold">Цвят:</label>
            <select
              value={color}
              onChange={(e) => setColor(e.target.value)}
              className="mt-1 block w-1/4 p-2 border rounded-md"
            >
              <option value="black-and-white">Черно-бяло</option>
              <option value="color">Цветно</option>
            </select>
          </div>

          {/* Paper Type */}
          <div className="">
            <label className="block text-gray-700 font-semibold">
              Тип хартия:
            </label>
            <select
              value={paperType}
              onChange={(e) => setPaperType(e.target.value)}
              className="mt-1 block w-1/4 p-2 border rounded-md"
            >
              <option value="standard">Стандартна</option>
              <option value="glossy">Гланцирана</option>
            </select>
          </div>
        </form>
        <a href="/userPayment">
          <button className="bg-primary text-white font-semibold p-2 rounded-lg hover:bg-primary-700 w-48 mt-4">
            Изпрати
          </button>
        </a>
      </div>
    </div>
  );
}
