package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.Appointment;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.PatientsResponse;
import edu.angelpina.physiocare.Models.RecordResponse;
import edu.angelpina.physiocare.Services.ServiceResponse;
import edu.angelpina.physiocare.Utils.MessageUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class RecordsController {
    public ListView recordList;
    private Stage s  tage;
    private Scene scene;
    Gson gson = new Gson();
    private Patient patient;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        String url = ServiceResponse.SERVER + "/records/patient/" + patient.getId();
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, RecordResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            recordList.getItems().setAll(response.getResultado());
                            recordList.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    Appointment selectedRecord = (Appointment) recordList.getSelectionModel().getSelectedItem();
                                    if (selectedRecord != null) {
                                        ActionDialog(selectedRecord);
                                    }
                                }
                            });
                        });
                    } else {
                        Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch patients"));
                    return null;
                });

    }

    public void ActionDialog(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Record Action");
        alert.setHeaderText("Choose an action for " + appointment.getDate());
        alert.setContentText("Select an option:");

        ButtonType editButton = new ButtonType("Edit");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(editButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == editButton) {
                editRecord(appointment);
            } else if (buttonType == deleteButton) {
                deleteRecord(appointment);
            }
            // Cancel button closes the popup automatically
        });
    }

    private void deleteRecord(Appointment patient) {
    }

    private void editRecord(Appointment patient) {
    }

    public void setPatient(Patient patient) {
        this.patient = patient;

        if (this.patient != null) {
            System.out.println("Patient ID: " + this.patient.getId());
            // Populate the view with patient data
        } else {
            System.out.println("Patient is null");
        }
    }

    public void goToTitle(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/angelpina/physiocare/Patients.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void addRecord(ActionEvent event) {
    }
}
