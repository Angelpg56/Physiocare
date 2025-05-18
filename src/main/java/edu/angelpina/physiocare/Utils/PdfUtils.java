package edu.angelpina.physiocare.Utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import edu.angelpina.physiocare.Models.Appointment;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.Physio;
import edu.angelpina.physiocare.Models.Record;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PdfUtils {
    public static void CreatePdfRecords(List<Record> records) {
        String dest = "temp/" + records.get(0).getPatient().getInsuranceNumber() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph patientInfo = new Paragraph("Patient Information:\n" +
                    "Name: " + records.get(0).getPatient().getName() + "\n" +
                    "Surname: " + records.get(0).getPatient().getSurname() + "\n" +
                    "Email: " + records.get(0).getPatient().getEmail() + "\n" +
                    "Insurance Number: " + records.get(0).getPatient().getInsuranceNumber() + "\n" +
                    "Address: " + records.get(0).getPatient().getAddress());
            document.add(patientInfo);

            float[] columnWidths = {2, 2}; // Adjust column ratios as needed
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addHeaderCell("Record ID");
            table.addHeaderCell("Medical Record");

            for (Record record : records) {
                if (record.getPatient() != null) {
                    table.addCell(record.get_id()).setFontSize(8);
                    table.addCell(record.getMedicalRecord()).setFontSize(8);
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

    public static void CreatePdfPatientAppointments(Record record) {
        String dest = "temp/" + record.get_id() + ".pdf";
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add clinic header
            Paragraph header = new Paragraph("PhysioCare Clinic\n123 Main Street, City, Country")
                    .setFontSize(14)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(header);

            // Add patient information
            Paragraph patientInfo = new Paragraph("Patient Information:\n" +
                    "Name: " + record.getPatient().getName() + "\n" +
                    "Surname: " + record.getPatient().getSurname() + "\n" +
                    "Email: " + record.getPatient().getEmail() + "\n" +
                    "Insurance Number: " + record.getPatient().getInsuranceNumber() + "\n" +
                    "Address: " + record.getPatient().getAddress());
            document.add(patientInfo);

            // Add record information
            Paragraph recordInfo = new Paragraph("\nRecord Information:\n" +
                    "Record ID: " + record.get_id() + "\n" +
                    "Medical Record: " + record.getMedicalRecord());
            document.add(recordInfo);

            // Add appointments table
            float[] columnWidths = {1, 1, 1, 1, 1, 1}; // Adjust column ratios as needed
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addHeaderCell("Appointment ID");
            table.addHeaderCell("Date");
            table.addHeaderCell("Physio");
            table.addHeaderCell("Diagnosis");
            table.addHeaderCell("Treatment");
            table.addHeaderCell("Observations");

            if (record.getAppointments() != null) {
                record.getAppointments().forEach(appointment -> {
                    table.addCell(new Paragraph(appointment.get_id()).setFontSize(8));
                    table.addCell(new Paragraph(appointment.getDate().toString()).setFontSize(8));
                    table.addCell(new Paragraph(appointment.getPhysio().getName() + " " + appointment.getPhysio().getSurname()).setFontSize(8));
                    table.addCell(new Paragraph(appointment.getDiagnosis()).setFontSize(8));
                    table.addCell(new Paragraph(appointment.getTreatment()).setFontSize(8));
                    table.addCell(new Paragraph(appointment.getObservations()).setFontSize(8));
                });
            }

            String bodyText = (record.getAppointments().size() < 10)
                    ? "Hello " + record.getPatient().getName() + ",\n\n" +
                        "This is a reminder that you have " + record.getAppointments().size() + " appointments done " +
                        "and only have " + (10 - record.getAppointments().size()) + " appointments left before reach" +
                        " the limit of appointments.\n\n" +
                        "Best regards,\nPhysioCare"
                    : "Hello " + record.getPatient().getName() + ",\n\n" +
                        "This is a reminder that you have reached the limit of appointments of 10.\n\n" +
                        "Best regards,\nPhysioCare";

            document.add(table);
            document.close();
            EmailSender.sendMail(
                    record.getPatient().getEmail(),
                    "apg17499@gmail.com",
                    "PhysioCare Appointment limit reminder",
                    bodyText,
                    dest);

            File file = new File(dest);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CreatePdfPhysioSalary(Physio physio, Map<Patient, List<Appointment>> appointmentsMap) {
        String dest = "temp/" + physio.getLicenseNumber() + "_Salary.pdf";
        double totalSalary = 1800.0; // Base salary
        double baseSalary = 1800.0;
        double pricePerAppointment = 50.0;

        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add clinic header
            Paragraph header = new Paragraph("PhysioCare Clinic\n123 Main Street, City, Country")
                    .setFontSize(14)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            document.add(header);

            // Add physio information
            Paragraph physioInfo = new Paragraph("Physio Information:\n" +
                    "Name: " + physio.getName() + " " + physio.getSurname() + "\n" +
                    "Email: " + physio.getEmail() + "\n" +
                    "Specialty: " + physio.getSpecialty() + "\n" +
                    "License Number: " + physio.getLicenseNumber());
            document.add(physioInfo);

                // Add appointments table
            float[] columnWidths = {1, 2, 2, 1}; // Adjust column ratios as needed
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addHeaderCell("Date");
            table.addHeaderCell("Patient");
            table.addHeaderCell("Treatment");
            table.addHeaderCell("Price");

            if (appointmentsMap.isEmpty()) {
                Paragraph noAppointments = new Paragraph("\nNo appointments done by this physio.")
                        .setFontSize(12)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
                document.add(noAppointments);
            } else {
                for (Map.Entry<Patient, List<Appointment>> entry : appointmentsMap.entrySet()) {
                    Patient patient = entry.getKey();
                    List<Appointment> appointments = entry.getValue();

                    for (Appointment appointment : appointments) {
                        table.addCell(new Paragraph(appointment.getDate().toString()).setFontSize(8));
                        table.addCell(new Paragraph(patient.getName() + " " + patient.getSurname()).setFontSize(8));
                        table.addCell(new Paragraph(appointment.getTreatment()).setFontSize(8));
                        table.addCell(new Paragraph("$" + pricePerAppointment).setFontSize(8));
                        totalSalary += pricePerAppointment;
                    }
                }

                document.add(table);
            }
            // Add total salary
            Paragraph total = new Paragraph("Base Salary: " + baseSalary + "€\nTotal Salary: " + totalSalary + "€")
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT);
            document.add(total);

            String bodyText = "Hello " + physio.getName() + ",\n\n" +
                    "I attach your monthly salary.\n\n" +
                    "Best regards,\nPhysioCare";

            document.close();
            EmailSender.sendMail(
                    physio.getEmail(),
                    "apg17499@gmail.com",
                    "PhysioCare Salary report",
                    bodyText,
                    dest);

            File file = new File(dest);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}