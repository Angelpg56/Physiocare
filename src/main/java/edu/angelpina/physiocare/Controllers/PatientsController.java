package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.PatientResponse;
import edu.angelpina.physiocare.Models.PatientsResponse;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class PatientsController {
    public Stage stage;
    public ListView patientsList;
    Gson gson = new Gson();
    private String user;
    private String pass;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        Platform.runLater(this::loadPatients);
    }

    private void loadPatients() {
        String url = ServiceResponse.SERVER + "/patients";
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientsResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {

                        Platform.runLater(() -> {
                            patientsList.getItems().setAll(response.getResultado());
                            patientsList.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    Patient selectedPatient = (Patient) patientsList.getSelectionModel().getSelectedItem();
                                    if (selectedPatient != null) {
                                        ActionDialog(selectedPatient);
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

    public void ActionDialog(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Patient Action");
        alert.setHeaderText("Records of " + patient.getName() + " " + patient.getSurname());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Full name:"), 0, 0);
        grid.add(new Label(patient.getName() + " " + patient.getSurname()), 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(new Label(patient.getEmail()), 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(new Label(patient.getAddress()), 1, 2);
        grid.add(new Label("Insurance Nº:"), 0, 3);
        grid.add(new Label(patient.getInsuranceNumber()), 1, 3);

        alert.getDialogPane().setContent(grid);

        ButtonType recordsButton = new ButtonType("Records");
        ButtonType editButton = new ButtonType("Edit");
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(recordsButton, editButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == editButton) {
                editPatient(patient);
            } else if (buttonType == deleteButton) {
                deletePatient(patient);
            } else if (buttonType == recordsButton) {
                showRecords(patient);
            }
            // Cancel button closes the popup automatically
        });
    }

    private void showRecords(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/angelpina/physiocare/Records.fxml"));
            Parent root = loader.load();
            RecordsController controller = loader.getController();
            //RecordsController controller = new RecordsController(patient);
            controller.setPatient(patient);
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

    private void showPatientForm(Patient patient) {
        // Create the dialog
        Dialog<Patient> dialog = new Dialog<>();
        dialog.setTitle(patient == null ? "Add Patient" : "Edit Patient");
        dialog.setHeaderText(patient == null ? "Fill the form to add a new patient" : "Edit the details of the patient");

        // Dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        DatePicker birthDateField = new DatePicker();
        birthDateField.setPromptText("Birth Date");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField insuranceNumberField = new TextField();
        insuranceNumberField.setPromptText("Insurance Number");

        // If editing, pre-fill the fields with the patient's data
        if (patient != null) {
            nameField.setText(patient.getName());
            surnameField.setText(patient.getSurname());
            emailField.setText(patient.getEmail());
            birthDateField.setValue(patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            addressField.setText(patient.getAddress());
            insuranceNumberField.setText(patient.getInsuranceNumber());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Birth Date:"), 0, 3);
        grid.add(birthDateField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Insurance Number:"), 0, 5);
        grid.add(insuranceNumberField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Disable the save button if fields are invalid
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Add validation for the fields
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.isEmpty() || !newValue.matches("^.+@.+\\..+$"));
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.isEmpty() || emailField.getText().isEmpty() || !emailField.getText().matches("^.+@.+\\..+$"));
        });

        surnameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.isEmpty() || emailField.getText().isEmpty() || !emailField.getText().matches("^.+@.+\\..+$"));
        });

        // Handle the dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Patient(
                    patient != null ? patient.getId() : null, // Keep the ID if editing
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    birthDateField.getValue() != null ? Date.from(birthDateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null,
                    addressField.getText(),
                    insuranceNumberField.getText(),
                    patient != null ? patient.getV() : 0 // Keep the version if editing
                );
            }
            return null;
        });

        Optional<Patient> result = dialog.showAndWait();
        result.ifPresent(updatedPatient -> {
            System.out.println(updatedPatient.toJson());
            if (patient == null) {
                // Action to add a new patient
                String url = ServiceResponse.SERVER + "/patients";
                ServiceResponse.getResponseAsync(url, updatedPatient.toJson(), "POST")
                        .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                        .thenAccept(response -> {
                            if (response.isOk()) {
                                Platform.runLater(() -> {
                                    MessageUtils.showMessage("Success", "Patient Created successfully");
                                    initialize();
                                });
                            } else {
                                Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                            }
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch patients"));
                            return null;
                        });
            } else {
                // Action to edit an existing patient
                String url = ServiceResponse.SERVER + "/patients/" + patient.getId();
                ServiceResponse.getResponseAsync(url, updatedPatient.toJson(), "PUT")
                        .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                        .thenAccept(response -> {
                            if (response.isOk()) {
                                Platform.runLater(() -> {
                                    MessageUtils.showMessage("Success", "Patient Edited successfully");
                                    initialize();
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
        });
    }

    public void addPatient(ActionEvent actionEvent) {
        showPatientForm(null); // Call the form with no patient for adding
    }

    private void editPatient(Patient patient) {
        showPatientForm(patient); // Call the form with a patient for editing
    }

    private void deletePatient(Patient patient) {
        String url = ServiceResponse.SERVER + "/patients/" + patient.getId();
        ServiceResponse.getResponseAsync(url, null, "DELETE")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            MessageUtils.showMessage("Success", "Patient deleted successfully");
                            patientsList.getItems().remove(patient);
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

    private void onePatient(String id) {
        String url = ServiceResponse.SERVER + "/patients/" + id;
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> System.out.println(response.getResultado()));
                    } else {
                        Platform.runLater(() -> System.out.println("Error: " + response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> System.out.println("Error: " + "Failed to fetch patients"));
                    return null;
                });
    }

    private void postConsult(String id) {
        String url = ServiceResponse.SERVER + "/records/" + id + "/appointments";
        String consult = "{\n" +
                "    \"date\": \"2024-02-10\",\n" +
                "    \"physio\": \"675dbcc19a6519df1e746758\",\n" +
                "    \"diagnosis\": \"Distensión de ligamentos de la rodilla\",\n" +
                "    \"treatment\": \"Rehabilitación con ejercicios de fortalecimiento\",\n" +
                "    \"observations\": \"Se recomienda evitar actividad intensa por 6 semanas\"\n" +
                "}";
        ServiceResponse.getResponseAsync(url, consult, "POST")
                .thenApply(json -> gson.fromJson(json, PatientResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> System.out.println("Success"));
                    } else {
                        Platform.runLater(() -> System.out.println("Error: " + response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> System.out.println("Error: " + "Failed to fetch patients"));
                    return null;
                });
    }

    public void goToTitle(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/angelpina/physiocare/Title.fxml"));
            Parent root = loader.load();
            TitleController controller = loader.getController();
            controller.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showError("Error", "Failed to load Records view");
        }
    }

}