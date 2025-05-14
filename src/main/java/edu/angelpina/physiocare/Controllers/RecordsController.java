package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.Appointment;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.RecordResponse;
import edu.angelpina.physiocare.Models.Record;
import edu.angelpina.physiocare.Services.ServiceResponse;
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

public class RecordsController {
    public ListView appointmentList;
    public Label titleLabel;
    public Label medicalRecordLabel;
    private Stage stage;
    private Scene scene;
    Gson gson = new Gson();
    private Patient patient;

    public RecordsController() {}

    public RecordsController(Patient patient) {
        this.patient = patient;
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (patient != null) {
                loadRecord();
            } else {
                MessageUtils.showError("Error", "Patient is null");
            }
        });
    }

    private void loadRecord() {
        titleLabel.setText("Records of " + patient.getName());
        String url = ServiceResponse.SERVER + "/records/patient/" + patient.getId();
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, RecordResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            appointmentList.getItems().setAll(response.getResultado());
                            appointmentList.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    Record selectedRecord = (Record) appointmentList.getSelectionModel().getSelectedItem();
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
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch records"));
                    return null;
                });
    }

    public void ActionDialog(Record record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Record Action");
        alert.setHeaderText("Records of " + patient.getName() + " " + patient.getSurname());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Full name:"), 0, 0);
        grid.add(new Label(patient.getName() + " " + patient.getSurname()), 1, 0);
        grid.add(new Label("Medical Record:"), 0, 1);
        grid.add(new Label(record.getMedicalRecord()), 1, 1);
        grid.add(new Label("Nº of appointments:"), 0, 2);
        grid.add(new Label(""+record.getAppointments().size()), 1, 2);

        alert.getDialogPane().setContent(grid);

        ButtonType detailsButton = new ButtonType("See More");
        ButtonType editButton = new ButtonType("Edit");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(detailsButton, editButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == editButton) {
                editRecord(record);
            } else if (buttonType == deleteButton) {
                deleteRecord(record);
            } else if(buttonType == detailsButton) {
                seeDetails(record);
            }
        });
    }

    private void seeDetails(Record record) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/angelpina/physiocare/Record.fxml"));
            Parent root = loader.load();
            RecordController controller = loader.getController();
            //RecordsController controller = new RecordsController(patient);
            controller.setPatient(patient);
            controller.setRecord(record);
            controller.setStage(stage);
            //loader.setController(controller);
            System.out.println(patient);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showError("Error", "Failed to load Records view");
        }
    }

    private void deleteRecord(Record record) {
    }

    private void editRecord(Record record) {
    }

    public void goToTitle(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/angelpina/physiocare/Patients.fxml"));
            Parent root = loader.load();
            PatientsController controller = loader.getController();
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

    public void addRecord(ActionEvent event) {
    }
}
