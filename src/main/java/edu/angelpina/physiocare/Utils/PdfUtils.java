package edu.angelpina.physiocare.Utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import edu.angelpina.physiocare.Models.Record;

import java.io.File;
import java.util.List;

public class PdfUtils {
    public static void CreatePdf(List<Record> records) {
        String dest = records.get(0).getPatient().getInsuranceNumber() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Record firstRecord = records.get(0);
            Paragraph patientInfo = new Paragraph("Patient Information:\n" +
                    "Name: " + firstRecord.getPatient().getName() + "\n" +
                    "Surname: " + firstRecord.getPatient().getSurname() + "\n" +
                    "Email: " + firstRecord.getPatient().getEmail() + "\n" +
                    "Insurance Number: " + firstRecord.getPatient().getInsuranceNumber() + "\n" +
                    "Address: " + firstRecord.getPatient().getAddress());
            document.add(patientInfo);

            float[] columnWidths = {2, 2}; // Adjust column ratios as needed
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addHeaderCell("Record ID");
            table.addHeaderCell("Medical Record");

            for (Record record : records) {
                if (record.getPatient() != null) {
                    table.addCell(record.get_id());
                    table.addCell(record.getMedicalRecord());
                }
            }

            document.add(table);
            document.close();
            ServiceUtils.uploadFile(dest);

            File file = new File(dest);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}