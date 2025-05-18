package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.*;
import edu.angelpina.physiocare.Models.Record;
import edu.angelpina.physiocare.Services.ServiceResponse;
import edu.angelpina.physiocare.Utils.EmailSender;
import edu.angelpina.physiocare.Utils.MessageUtils;
import edu.angelpina.physiocare.Utils.PdfUtils;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecordController {
    public Label titleLabel;
    public ListView appointmentList;
    public Label medicalRecordLabel;
    public Button btnAdd;
    private Stage stage;
    private Scene scene;
    Gson gson = new Gson();
    private Record record;
    private Patient patient;

    public void setPatient(Patient patient) {
        this.patient = patient;

        if (this.patient != null) {
            System.out.println("Patient ID: " + this.patient.getId());
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
                if (record.getAppointments().size() >= 10) {
                    btnAdd.setDisable(true);
                }
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

        alert.showAndWait();
    }

    public void addAppointment(ActionEvent event) {
        String url1 = ServiceResponse.SERVER + "/physios";
        ServiceResponse.getResponseAsync(url1, null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysiosResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            List<Physio> physiosList = response.getResultado();

                            Dialog<Appointment> dialog = new Dialog<>();
                            dialog.setTitle("Add Appointment");
                            dialog.setHeaderText("Fill the form to add a new appointment for " + patient.getName() + " " + patient.getSurname());

                            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                            GridPane grid = new GridPane();
                            grid.setHgap(10);
                            grid.setVgap(10);
                            grid.setPadding(new Insets(20, 150, 10, 10));

                            DatePicker dateField = new DatePicker();
                            TextField diagnosisField = new TextField();
                            diagnosisField.setPromptText("Diagnosis");
                            TextField treatmentField = new TextField();
                            treatmentField.setPromptText("Treatment");
                            TextArea observationsField = new TextArea();
                            observationsField.setPromptText("Observations");
                            observationsField.setWrapText(true);

                            ComboBox<Physio> physioComboBox = new ComboBox<>();
                            physioComboBox.getItems().addAll(physiosList);
                            physioComboBox.setPromptText("Select Physio");

                            grid.add(new Label("Date:"), 0, 0);
                            grid.add(dateField, 1, 0);
                            grid.add(new Label("Diagnosis:"), 0, 1);
                            grid.add(diagnosisField, 1, 1);
                            grid.add(new Label("Treatment:"), 0, 2);
                            grid.add(treatmentField, 1, 2);
                            grid.add(new Label("Observations:"), 0, 3);
                            grid.add(observationsField, 1, 3);
                            grid.add(new Label("Physio:"), 0, 4);
                            grid.add(physioComboBox, 1, 4);

                            dialog.getDialogPane().setContent(grid);

                            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
                            saveButton.setDisable(true);

                            // Validation
                            dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
                                saveButton.setDisable(newValue == null || diagnosisField.getText().trim().isEmpty() || physioComboBox.getValue() == null);
                            });

                            diagnosisField.textProperty().addListener((observable, oldValue, newValue) -> {
                                saveButton.setDisable(newValue.trim().isEmpty() || dateField.getValue() == null || physioComboBox.getValue() == null);
                            });

                            physioComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                                saveButton.setDisable(newValue == null || dateField.getValue() == null || diagnosisField.getText().trim().isEmpty());
                            });

                            dialog.setResultConverter(dialogButton -> {
                                if (dialogButton == saveButtonType) {
                                    return new Appointment(
                                            Date.valueOf(dateField.getValue()),
                                            physioComboBox.getValue(),
                                            diagnosisField.getText(),
                                            treatmentField.getText(),
                                            observationsField.getText()
                                    );
                                }
                                return null;
                            });

                            Optional<Appointment> result = dialog.showAndWait();
                            result.ifPresent(newAppointment -> {
                                // Send the new appointment to the server
                                String url2 = ServiceResponse.SERVER + "/records/" + record.get_id() + "/appointments";
                                System.out.println("URL: " + url2);
                                ServiceResponse.getResponseAsync(url2, newAppointment.toJson(), "POST")
                                        .thenApply(json -> gson.fromJson(json, RecordResponse.class))
                                        .thenAccept(response2 -> {
                                            if (response2.isOk()) {
                                                Platform.runLater(() -> {
                                                    this.record = response2.getResultado();
                                                    MessageUtils.showMessage("Success", "Appointment Created successfully");
                                                    initialize(); // Reload the appointments list
                                                    if (this.record.getAppointments().size() >= 8) {
                                                        PdfUtils.CreatePdfPatientAppointments(this.record);
                                                    }
                                                });
                                            } else {
                                                Platform.runLater(() -> MessageUtils.showError("Error", response2.getError()));
                                            }
                                        })
                                        .exceptionally(ex -> {
                                            Platform.runLater(() -> MessageUtils.showError("Error", "Failed to create Appointment"));
                                            return null;
                                        });
                            });
                        });
                    } else {
                        Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch physios"));
                    return null;
                });
    }

    public void goBack(ActionEvent event) {
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
