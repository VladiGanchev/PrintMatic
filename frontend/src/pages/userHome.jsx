import { useState } from "react";
import { useDropzone } from "react-dropzone";
import HeaderUser from "../components/headerUser";
import { useNavigate } from "react-router-dom";
import { uploadFile, createOrder } from "../services/orderService";

export default function UserHome() {
  let navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [copies, setCopies] = useState(1);
  const [color, setColor] = useState("true");
  const [paperType, setPaperType] = useState("REGULAR_MATE");
  const [doubleSided, setDoubleSided] = useState("false")
  const [pageSize, setPageSize] = useState("A4")
  const [deadline, setDeadline] = useState("THREE_DAYS")
  const [comment, setComment] = useState("");
  const [title, setTitle] = useState("")
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleDoubleSidedChange = (event) => {
    setDoubleSided(event.target.value);
  };

  const onDrop = (acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      setFile(acceptedFiles[0]);
      setTitle(acceptedFiles[0].name);
    }
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      "application/pdf": [".pdf"],
      //"application/msword": [".doc", ".docx"],
    },
    maxFiles: 1,
  });

  const handleSubmit = async () =>{
    setIsLoading(true)
    setError(null)

    try{
        if(!file){
          throw new Error("Моля изберете желания от Вас файл")
        }
        
        const fileData = await uploadFile(file, color === "true")

        console.log(fileData)

        const orderData = {
          title: title,
          copies: parseInt(copies),
          doubleSided: doubleSided === "true",
          pageSize: pageSize,
          paperType: paperType,
          additionalInfo: comment,
          deadline: deadline,
          fileUrl: fileData.blobName,
          totalPages: fileData.totalPages,
          colorfulPages: fileData.colorfulPages,
          grayscalePages: fileData.grayscalePages
        };

        console.log(orderData)

        await createOrder(orderData)
        navigate("/userPayment")

    }catch(error){
      setError(error.message || "Възникна грешка");
      Console.log(error)
    }finally{
      setIsLoading(false);
    }
  }

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12">
        <p className="text-5xl font-bold">Нова заявка</p>
        <div
          {...getRootProps()}
          className={`mt-8 w-1/2 h-40 flex flex-col hover:cursor-pointer items-center justify-center border-2 border-dashed rounded-lg p-4 ${isDragActive ? "border-blue-500 bg-blue-100" : "border-gray-400"
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
          {file && (
            <div className="text-gray-700">
              {file.name} ({(file.size / 1024).toFixed(2)} KB)
            </div>
          )}
        </div>

        <form className="flex flex-row my-8">
          <div className="flex flex-col w-1/3 space-y-3">
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
                className="mt-1 block p-2 border rounded-md w-[75%]"
              />
            </div>

            {/* Color Options */}
            <div>
              <label className="block text-gray-700 font-semibold">Цвят:</label>
              <select
                value={color}
                onChange={(e) => setColor(e.target.value)}
                className="mt-1 block p-2 border rounded-md w-[75%]"
              >
                <option value="true">Черно-бяло</option>
                <option value="false">Цветно</option>
              </select>
            </div>

            {/* Paper Type */}
            <div>
              <label className="block text-gray-700 font-semibold">
                Тип хартия:
              </label>
              <select
                value={paperType}
                onChange={(e) => setPaperType(e.target.value)}
                className="mt-1 block p-2 border rounded-md w-[75%]"
              >
                <option value="REGULAR_MATE">Стандартна</option>
                <option value="GLOSSY">Гланцирана</option>
                <option value="BRIGHT_WHITE">Ярко бяла</option>
                <option value="PHOTO">Фотохартия</option>
                <option value="HEAVYWEIGHT">Тежка (плътна) хартия</option>
              </select>
            </div>
          </div>
          <div className="flex flex-col space-y-3 w-1/3">
            <div>
              {/* Paper Size */}
              <label className="block text-gray-700 font-semibold">
                Размер на хартия:
              </label>
              <select
                value={pageSize}
                onChange={(e) => setPageSize(e.target.value)}
                className="mt-1 block p-2 border rounded-md w-[75%]"
              >
                <option value="A3">А3</option>
                <option value="A4">А4</option>
                <option value="A5">А5</option>
              </select>
            </div>
            <div>
              {/* Deadline */}
              <label className="block text-gray-700 font-semibold">
                Краен срок на изпълнение:
              </label>
              <select
                value={deadline}
                onChange={(e) => setDeadline(e.target.value)}
                className="mt-1 block p-2 border rounded-md w-[75%]"
              >
                <option value="ONE_HOUR">До час</option>
                <option value="ONE_DAY">До ден</option>
                <option value="THREE_DAYS">До три дни</option>
                <option value="ONE_WEEK">До седмица</option>
              </select>
            </div>
            <div>
              <label className="block text-gray-700 font-semibold">
                Вид принтиране:
              </label>
              <label className="flex items-center space-x-3">
                <input
                  type="radio"
                  value="false"
                  name="example"
                  checked={doubleSided === 'false'}
                  onChange={handleDoubleSidedChange}
                  className="form-radio h-5 w-5 text-blue-600"
                />
                <span className="text-gray-700">Едностранно принтиране</span>
              </label>
              <label className="flex items-center space-x-3">
                <input
                  type="radio"
                  value="true"
                  name="example"
                  checked={doubleSided === 'true'}
                  onChange={handleDoubleSidedChange}
                  className="form-radio h-5 w-5 text-blue-600"
                />
                <span className="text-gray-700">Двустранно принтиране</span>
              </label>
            </div>
          </div>
          <div className="flex flex-col space-y-3 w-1/3">
            <label className="block text-gray-700 font-semibold">
              Допълнителен коментар:
            </label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Добавете коментар към поръчката..."
              className="w-full h-32 p-2 border rounded-md resize-none focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
            />
          </div>
        </form>

        <div className="flex justify-center">
          <button
            className="bg-primary text-white font-semibold p-2 rounded-lg hover:bg-primary-700 w-48 mt-4 disabled:opacity-50"
            onClick={handleSubmit}
            disabled={isLoading || !file}
          >
            {isLoading ? "Изпращане..." : "Изпрати"}
          </button>
        </div>

      </div>
    </div>
  );
}
