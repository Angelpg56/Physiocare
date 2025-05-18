package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.*;
import edu.angelpina.physiocare.Models.Record;
import edu.angelpina.physiocare.Services.ServiceResponse;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PhysiosController {
    public ListView physiosList;
    private List<Physio> physios;
    public Button btnAdd;
    public Button btnSSalary;
    Gson gson = new Gson();
    public Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        Platform.runLater(this::loadPhysios);
    }

    private void loadPhysios() {
        String url = ServiceResponse.SERVER + "/physios";
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, PhysiosResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            physiosList.getItems().setAll(response.getResultado());
                            physios = response.getResultado();

                            physiosList.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2) {
                                    Physio selectedPhysio = (Physio) physiosList.getSelectionModel().getSelectedItem();
                                    if (selectedPhysio != null) {
                                        ActionDialog(selectedPhysio);
                                    }
                                }
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

        if(ServiceResponse.getUserRol().equals("physio")) {
            btnAdd.setDisable(true);
            btnSSalary.setDisable(true);
        }
    }

    public void ActionDialog(Physio physio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Physio Action");
        alert.setHeaderText("Details of " + physio.getName() + " " + physio.getSurname());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Full name:"), 0, 0);
        grid.add(new Label(physio.getName() + " " + physio.getSurname()), 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(new Label(physio.getEmail()), 1, 1);
        grid.add(new Label("Speciality:"), 0, 2);
        grid.add(new Label(physio.getSpecialty()), 1, 2);
        grid.add(new Label("License Nº:"), 0, 3);
        grid.add(new Label(physio.getLicenseNumber()), 1, 3);

        alert.getDialogPane().setContent(grid);

        if(ServiceResponse.getUserRol().equals("admin")) {
            alert.setContentText("Select an option:");

            ButtonType editButton = new ButtonType("Edit");
            ButtonType deleteButton = new ButtonType("Delete");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(editButton, deleteButton, cancelButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == editButton) {
                    editPhysio(physio);
                } else if (buttonType == deleteButton) {
                    deletePhysio(physio);
                }
                // Cancel button closes the popup automatically
            });
        }
    }

    private void showPhysioForm(Physio physio) {
        // Crear el diálogo
        Dialog<Physio> dialog = new Dialog<>();
        dialog.setTitle(physio == null ? "Add Physio" : "Edit Physio");
        dialog.setHeaderText(physio == null ? "Fill the form to add a new physio" : "Edit the details of the physio");

        // Botones del diálogo
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Crear el formulario
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
        ComboBox<String> specialtyField = new ComboBox<>();
        specialtyField.getItems().addAll("Sports", "Neurological", "Pediatric", "Geriatric", "Oncological");
        specialtyField.setPromptText("Specialty");
        TextField licenseNumberField = new TextField();
        licenseNumberField.setPromptText("License Number");

        // Si es edición, rellenar los campos con los valores del Physio
        if (physio != null) {
            nameField.setText(physio.getName());
            surnameField.setText(physio.getSurname());
            emailField.setText(physio.getEmail());
            specialtyField.setValue(physio.getSpecialty());
            licenseNumberField.setText(physio.getLicenseNumber());
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Specialty:"), 0, 3);
        grid.add(specialtyField, 1, 3);
        grid.add(new Label("License Number:"), 0, 4);
        grid.add(licenseNumberField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Deshabilitar el botón de guardar si los campos no son válidos
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validar los campos
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(
                    !newValue.matches("^.+@.+\\..+$") ||
                    !licenseNumberField.getText().matches("^[a-zA-Z0-9]{8}$"));
        });

        licenseNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(
                    !newValue.matches("^[a-zA-Z0-9]{8}$") ||
                    !emailField.getText().matches("^.+@.+\\..+$"));
        });

        specialtyField.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(
                    newValue == null ||
                    !emailField.getText().matches("^.+@.+\\..+$") ||
                    !licenseNumberField.getText().matches("^[a-zA-Z0-9]{8}$"));
        });

        // Manejar el resultado del diálogo
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Physio(
                    physio != null ? physio.getId() : null, // Mantener el ID si es edición
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    specialtyField.getValue(),
                    licenseNumberField.getText(),
                    physio != null ? physio.getV() : 0 // Mantener el valor de __v si es edición
                );
            }
            return null;
        });

        Optional<Physio> result = dialog.showAndWait();
        result.ifPresent(updatedPhysio -> {
            if (physio == null) {
                // Acción para agregar un nuevo Physio
                String url = ServiceResponse.SERVER + "/physios";
                ServiceResponse.getResponseAsync(url, updatedPhysio.toJson(), "POST")
                        .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                        .thenAccept(response -> {
                            if (response.isOk()) {
                                Platform.runLater(() -> {
                                    MessageUtils.showMessage("Success", "Physio Created successfully");
                                    initialize();
                                });
                            } else {
                                Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                            }
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch Physios"));
                            return null;
                        });
            } else {
                // Acción para editar un Physio existente
                String url = ServiceResponse.SERVER + "/physios/" + physio.getId();
                ServiceResponse.getResponseAsync(url, updatedPhysio.toJson(), "PUT")
                        .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                        .thenAccept(response -> {
                            if (response.isOk()) {
                                Platform.runLater(() -> {
                                    MessageUtils.showMessage("Success", "Physio Edited successfully");
                                    initialize();
                                });
                            } else {
                                Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                            }
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch Physios"));
                            return null;
                        });
            }
        });
    }

    public void addPhysio(ActionEvent actionEvent) {
        showPhysioForm(null); // Llamar al formulario vacío para agregar
    }

    private void editPhysio(Physio physio) {
        showPhysioForm(physio); // Llamar al formulario con datos para editar
    }

    private void deletePhysio(Physio physio) {
        String url = ServiceResponse.SERVER + "/physios/" + physio.getId();
        ServiceResponse.getResponseAsync(url, null, "DELETE")
                .thenApply(json -> gson.fromJson(json, PhysioResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            MessageUtils.showMessage("Success", "Physios deleted successfully");
                            physiosList.getItems().remove(physio);
                        });
                    } else {
                        Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch Physios"));
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

    public void SendSalary(ActionEvent event) {

        String url = ServiceResponse.SERVER + "/records";
        ServiceResponse.getResponseAsync(url, null, "GET")
                .thenApply(json -> gson.fromJson(json, RecordsResponse.class))
                .thenAccept(response -> {
                    if (response.isOk()) {
                        Platform.runLater(() -> {
                            Map<Physio, Map<Patient, List<Appointment>>> physioAppointmentsMap =
                                    organizeByPhysios(response.getResultado());

                            // show physioAppointmentsMap in console
                            /*for (Map.Entry<Physio, Map<Patient, List<Appointment>>> entry : physioAppointmentsMap.entrySet()) {
                                System.out.println("Physio: " + entry.getKey().getName());
                                for (Map.Entry<Patient, List<Appointment>> patientEntry : entry.getValue().entrySet()) {
                                    System.out.println("  Patient: " + patientEntry.getKey().getName());
                                    for (Appointment appointment : patientEntry.getValue()) {
                                        System.out.println("    Appointment: " + appointment.getDate());
                                    }
                                }
                            }*/

                            // Generate PDFs for each Physio
                            for (Map.Entry<Physio, Map<Patient, List<Appointment>>> entry : physioAppointmentsMap.entrySet()) {
                                PdfUtils.CreatePdfPhysioSalary(entry.getKey(), entry.getValue());
                            }
                        });
                    } else {
                        Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace(); // Log the exception
                    Platform.runLater(() -> MessageUtils.showError("Error", "Failed to fetch Records"));
                    return null;
                });
    }

    public Map<Physio, Map<Patient, List<Appointment>>> organizeByPhysios(List<Record> records) {
        Map<Physio, Map<Patient, List<Appointment>>> result = new HashMap<>();

        for (Physio physio : physios) {
            result.put(physio, new HashMap<>());
        }

        for (Record record : records) {
            Patient patient = record.getPatient();
            List<Appointment> appointments = record.getAppointments();

            for (Appointment appointment : appointments) {
                AtomicBoolean found = new AtomicBoolean(false);
                result.keySet().forEach(key -> {
                    if (key.getId().equals(appointment.getPhysio().getId())) {
                        result.get(key).keySet().forEach( k -> {
                                    if (k.getId().equals(patient.getId())) {
                                        result.get(key).get(k).add(appointment);
                                        found.set(true);
                                    }
                        });
                        if (!found.get()) {
                            result.get(key).put(patient, new ArrayList<>());
                            result.get(key).get(patient).add(appointment);
                        }
                    }
                });
            }
        }

        return result;
    }
}
