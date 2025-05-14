package edu.angelpina.physiocare.Controllers;

import edu.angelpina.physiocare.Models.Appointment;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.Record;
import edu.angelpina.physiocare.Utils.MessageUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RecordController {
    public Label titleLabel;
    public ListView appointmentList;
    public Label medicalRecordLabel;
    private Stage stage;
    private Scene scene;
    private Record record;
    private Patient patient;

    public void setPatient(Patient patient) {
        this.patient = patient;

        if (this.patient != null) {
            System.out.println("Patient ID: " + this.patient.getId());
            // Populate the view with patient data
        } else {
            System.out.println("Patient is null");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRecord(Record record) {
        this.record = record;

        if (this.record != null) {
            System.out.println("Record ID: " + this.record.get_id());
        } else {
            System.out.println("Record is null");
        }
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (record != null) {
                titleLabel.setText("Record of " + patient.getName());
                medicalRecordLabel.setText("Medical Record: " + record.getMedicalRecord());
                appointmentList.getItems().clear();
                appointmentList.getItems().addAll(record.getAppointments());
                appointmentList.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        Appointment selectedAppointment = (Appointment) appointmentList.getSelectionModel().getSelectedItem();
                        if (selectedAppointment != null) {
                            ActionDialog(selectedAppointment);
                        }
                    }
                });
            } else {
                System.out.println("Record is null");
            }
        });
    }

    private void ActionDialog(Appointment selectedAppointment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Appointment Action");
        alert.setHeaderText("Appointment of " + patient.getName() + " day " + selectedAppointment.getDate());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Medical Record:"), 0, 0);
        grid.add(new Label(record.getMedicalRecord()), 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(new Label(selectedAppointment.getDate().toString()), 1, 1);
        grid.add(new Label("Physio:"), 0, 2);
        grid.add(new Label(selectedAppointment.getPhysio().getName() + " " + selectedAppointment.getPhysio().getSurname()), 1, 2);
        grid.add(new Label("Diagnosis:"), 0, 3);
        grid.add(new Label(selectedAppointment.getDiagnosis()), 1, 3);
        grid.add(new Label("Treatment:"), 0, 4);
        grid.add(new Label(selectedAppointment.getTreatment()), 1, 4);
        grid.add(new Label("Observations:"), 0, 5);
        grid.add(new Label(selectedAppointment.getObservations()), 1, 5);

        alert.getDialogPane().setContent(grid);

        //ButtonType recordsButton = new ButtonType("Records");
        //ButtonType editButton = new ButtonType("Edit");
        //ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        //alert.getButtonTypes().setAll(recordsButton, editButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            /*if (buttonType == editButton) {
                editPatient(patient);
            } else if (buttonType == deleteButton) {
                deletePatient(patient);
            } else if (buttonType == recordsButton) {
                showRecords(patient);
            }*/
            // Cancel button closes the popup automatically
        });
    }

    public void addRecord(ActionEvent event) {
    }

    public void goToTitle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/angelpina/physiocare/Records.fxml"));
            Parent root = loader.load();
            RecordsController controller = loader.getController();
            controller.setPatient(patient);
            controller.setStage(stage);
            System.out.println(patient);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showError("Error", "Failed to load Records view");
        }
    }
}
