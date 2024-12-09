import { useState } from "react";
import { Worker, Viewer } from "@react-pdf-viewer/core";
import "@react-pdf-viewer/core/lib/styles/index.css";
import "@react-pdf-viewer/default-layout/lib/styles/index.css";
import mammoth from "mammoth";
import HeaderUser from "../components/headerUser";

export default function UserHistoryDocument() {
  const [isPdf] = useState(true); // TODO: Check if document is PDF on load
  const [docxContent, setDocxContent] = useState("");
  const filePath = isPdf ? "/sample.pdf" : "/sample.docx";

  const loadDocxFile = async (file) => {
    try {
      const response = await fetch(file);
      const arrayBuffer = await response.arrayBuffer();
      const { value } = await mammoth.extractRawText({ arrayBuffer });
      setDocxContent(value);
    } catch (error) {
      console.error("Error loading DOCX file:", error);
    }
  };

  if (!isPdf && docxContent === "") {
    loadDocxFile(filePath);
  }

  return (
    <div className="w-screen h-screen flex flex-row hover:cursor-default">
      <HeaderUser />
      <div className="w-full h-screen flex flex-col p-12 overflow-scroll">
        <p className="text-5xl font-bold">Документ1.docx - готов</p>
        <div className="mt-8 border rounded-lg p-4 bg-gray-100 h-[80vh]">
          {isPdf ? (
            <Worker
              workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}
            >
              <Viewer fileUrl={filePath} />
            </Worker>
          ) : (
            <div className="prose max-w-none">
              {docxContent || "Зареждаме документа..."}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
