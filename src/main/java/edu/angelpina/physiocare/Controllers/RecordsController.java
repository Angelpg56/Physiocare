package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.*;
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
import java.util.Optional;

public class RecordsController {
    public ListView recordsList;
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
                .thenApply(json -> gson.fromJson(json, RecordsResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            recordsList.getItems().setAll(response.getResultado());
                            recordsList.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    Record selectedRecord = (Record) recordsList.getSelectionModel().getSelectedItem();
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
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(detailsButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == deleteButton) {
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
        String url = ServiceResponse.SERVER + "/records/" + record.get_id();
        ServiceResponse.getResponseAsync(url, null, "DELETE")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            MessageUtils.showMessage("Success", "Record deleted successfully");
                            recordsList.getItems().remove(record);
                        });
                    } else {
                        Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to delete Records"));
                    return null;
                });
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
        Dialog<Record> dialog = new Dialog<>();
        dialog.setTitle("Add Record");
        dialog.setHeaderText("Fill the form to add a new record for " + patient.getName() + " " + patient.getSurname());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField medicalRecordField = new TextField();
        medicalRecordField.setPromptText("Medical Record");

        grid.add(new Label("Medical Record:"), 0, 0);
        grid.add(medicalRecordField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        medicalRecordField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Record(patient, medicalRecordField.getText());
            }
            return null;
        });

        Optional<Record> result = dialog.showAndWait();
        result.ifPresent(newRecord -> {
            // Acción para agregar un nuevo Record
            String url = ServiceResponse.SERVER + "/records";
            ServiceResponse.getResponseAsync(url, newRecord.toJson(), "POST")
                    .thenApply(json -> gson.fromJson(json, RecordResponse.class))
                    .thenAccept(response -> {
                        if (response.isOk()) {
                            Platform.runLater(() -> {
                                MessageUtils.showMessage("Success", "Record Created successfully");
                                loadRecord(); // Recargar la lista de records
                            });
                        } else {
                            Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> MessageUtils.showError("Error", "Failed to create Record"));
                        return null;
                    });
        });
    }
}
