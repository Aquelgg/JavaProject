package com.projektjava;

import com.projektjava.db.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import all controls
import javafx.scene.control.cell.PropertyValueFactory; // For TableView
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public class MainController {

    @FXML
    private TableView<Task> zadaniaTableView;
    @FXML
    private TableColumn<Task, Integer> idZadaniaColumn;
    @FXML
    private TableColumn<Task, String> tytulColumn;
    @FXML
    private TableColumn<Task, String> opisColumn;
    @FXML
    private TableColumn<Task, LocalDate> dataWykonaniaColumn;
    @FXML
    private TableColumn<Task, String> statusColumn;

    @FXML
    private Label imieLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField tytulZadaniaField;
    @FXML
    private TextArea opisZadaniaArea;
    @FXML
    private DatePicker dataWykonaniaPicker;

    private int selectedTaskIdForEdit = -1;

    @FXML
    private TextField tytulEdycjaField;
    @FXML
    private TextArea opisEdycjaArea;
    @FXML
    private DatePicker dataWykonaniaEdycjaPicker;
    @FXML
    private ComboBox<String> statusEdycjaComboBox;

    private int aktualnyIdUzytkownika;

    @FXML
    private void initialize() {
        // Initialize TableView columns
        idZadaniaColumn.setCellValueFactory(new PropertyValueFactory<>("idZadania"));
        tytulColumn.setCellValueFactory(new PropertyValueFactory<>("tytul"));
        opisColumn.setCellValueFactory(new PropertyValueFactory<>("opis"));
        dataWykonaniaColumn.setCellValueFactory(new PropertyValueFactory<>("dataWykonania"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        ObservableList<String> statusy = FXCollections.observableArrayList("oczekuje", "wykonane");
        statusEdycjaComboBox.setItems(statusy);

        zadaniaTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        parseAndPopulateEditFields(newValue);
                    }
                }
        );
    }

    public void ustawDaneUzytkownika(String imie, String email, int id_uzytkownika) {
        imieLabel.setText("Imię: " + imie);
        emailLabel.setText("Email: " + email);
        this.aktualnyIdUzytkownika = id_uzytkownika;
        zaladujZadaniaUzytkownika();
    }

    private void zaladujZadaniaUzytkownika() {
        ObservableList<Task> listaZadan = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT pz.id_zadania, z.tytul, z.opis, z.data_wykonania, z.status " +
                    "FROM PrzypisanieZadania pz JOIN Zadanie z ON pz.id_zadania = z.id_zadania " +
                    "WHERE pz.id_uzytkownika = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, aktualnyIdUzytkownika);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_zadania");
                String tytul = rs.getString("tytul");
                String opis = rs.getString("opis");
                LocalDate dataWykonania = (rs.getDate("data_wykonania") != null) ? rs.getDate("data_wykonania").toLocalDate() : null;
                String status = rs.getString("status");

                listaZadan.add(new Task(id, tytul, opis, dataWykonania, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się załadować zadań: " + e.getMessage());
        }

        zadaniaTableView.setItems(listaZadan);
    }

    @FXML
    private void wyloguj(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/projektjava/hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Logowanie");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się wylogować: " + e.getMessage());
        }
    }

    @FXML
    private void zmienStatusNaZrobione() {
        Task selectedTask = zadaniaTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Brak zaznaczenia", "Proszę zaznaczyć zadanie do zmiany statusu.");
            return;
        }

        int taskId = selectedTask.getIdZadania();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "UPDATE Zadanie SET status = 'wykonane' WHERE id_zadania = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, taskId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Status zadania zmieniono na 'wykonane'.");
                zaladujZadaniaUzytkownika();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zmienić statusu zadania.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Błąd podczas zmiany statusu: " + e.getMessage());
        }
    }

    @FXML
    private void zmienStatusNaOczekuje() {
        Task selectedTask = zadaniaTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Brak zaznaczenia", "Proszę zaznaczyć zadanie do zmiany statusu.");
            return;
        }

        int taskId = selectedTask.getIdZadania();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "UPDATE Zadanie SET status = 'oczekuje' WHERE id_zadania = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, taskId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Status zadania zmieniono na 'oczekuje'.");
                zaladujZadaniaUzytkownika();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zmienić statusu zadania.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Błąd podczas zmiany statusu: " + e.getMessage());
        }
    }

    @FXML
    private void usunZadanie() {
        Task selectedTask = zadaniaTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Brak zaznaczenia", "Proszę zaznaczyć zadanie do usunięcia.");
            return;
        }

        int taskId = selectedTask.getIdZadania();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Potwierdź usunięcie");
        confirmAlert.setHeaderText("Czy na pewno chcesz usunąć to zadanie?");
        confirmAlert.setContentText("Zadanie zostanie trwale usunięte z bazy danych.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnector.connect()) {
                String deleteAssignmentQuery = "DELETE FROM PrzypisanieZadania WHERE id_zadania = ?";
                PreparedStatement assignStmt = conn.prepareStatement(deleteAssignmentQuery);
                assignStmt.setInt(1, taskId);
                assignStmt.executeUpdate();

                String deleteTaskQuery = "DELETE FROM Zadanie WHERE id_zadania = ?";
                PreparedStatement taskStmt = conn.prepareStatement(deleteTaskQuery);
                taskStmt.setInt(1, taskId);
                int rowsAffected = taskStmt.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie zostało usunięte.");
                    zaladujZadaniaUzytkownika();
                    clearEditFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się usunąć zadania.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Błąd podczas usuwania zadania: " + e.getMessage());
            }
        }
    }

    @FXML
    private void dodajZadanie() {
        String tytul = tytulZadaniaField.getText();
        String opis = opisZadaniaArea.getText();
        LocalDate localDate = dataWykonaniaPicker.getValue();

        if (tytul.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak tytułu", "Proszę wprowadzić tytuł zadania.");
            return;
        }

        Date sqlDate = null;
        if (localDate != null) {
            sqlDate = Date.valueOf(localDate);
        }

        try (Connection conn = DatabaseConnector.connect()) {
            conn.setAutoCommit(false);

            String insertTaskQuery = "INSERT INTO Zadanie (tytul, opis, data_wykonania, status) VALUES (?, ?, ?, ?)";
            PreparedStatement taskStmt = conn.prepareStatement(insertTaskQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            taskStmt.setString(1, tytul);
            taskStmt.setString(2, opis);
            taskStmt.setDate(3, sqlDate);
            taskStmt.setString(4, "oczekuje");
            taskStmt.executeUpdate();

            ResultSet rs = taskStmt.getGeneratedKeys();
            int newTaskId = -1;
            if (rs.next()) {
                newTaskId = rs.getInt(1);
            }

            if (newTaskId != -1) {
                String assignTaskQuery = "INSERT INTO PrzypisanieZadania (id_uzytkownika, id_zadania) VALUES (?, ?)";
                PreparedStatement assignStmt = conn.prepareStatement(assignTaskQuery);
                assignStmt.setInt(1, aktualnyIdUzytkownika);
                assignStmt.setInt(2, newTaskId);
                assignStmt.executeUpdate();

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie zostało dodane i przypisane do użytkownika.");
                zaladujZadaniaUzytkownika();
                clearAddTaskFields();
            } else {
                conn.rollback();
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać ID nowego zadania.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Błąd podczas dodawania zadania: " + e.getMessage());
            try (Connection conn = DatabaseConnector.connect()) {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    @FXML
    private void rozpocznijEdycje() {
        Task selectedTask = zadaniaTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "Brak zaznaczenia", "Proszę zaznaczyć zadanie do edycji.");
            return;
        }
        parseAndPopulateEditFields(selectedTask);
    }

    @FXML
    private void edytujZadanie() {
        if (selectedTaskIdForEdit == -1) {
            showAlert(Alert.AlertType.WARNING, "Brak wybranego zadania", "Proszę wybrać zadanie do edycji, klikając przycisk 'Edytuj Wybrane Zadanie'.");
            return;
        }

        String tytul = tytulEdycjaField.getText();
        String opis = opisEdycjaArea.getText();
        LocalDate localDate = dataWykonaniaEdycjaPicker.getValue();
        String status = statusEdycjaComboBox.getValue();

        if (tytul.isEmpty() || status == null || status.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Puste pola", "Tytuł i status są wymagane.");
            return;
        }

        Date sqlDate = null;
        if (localDate != null) {
            sqlDate = Date.valueOf(localDate);
        }

        try (Connection conn = DatabaseConnector.connect()) {
            String checkOwnershipQuery = "SELECT COUNT(*) FROM PrzypisanieZadania WHERE id_uzytkownika = ? AND id_zadania = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkOwnershipQuery);
            checkStmt.setInt(1, aktualnyIdUzytkownika);
            checkStmt.setInt(2, selectedTaskIdForEdit);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                showAlert(Alert.AlertType.ERROR, "Błąd autoryzacji", "Nie masz uprawnień do edycji tego zadania.");
                clearEditFields();
                return;
            }

            String query = "UPDATE Zadanie SET tytul = ?, opis = ?, data_wykonania = ?, status = ? WHERE id_zadania = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tytul);
            stmt.setString(2, opis);
            stmt.setDate(3, sqlDate);
            stmt.setString(4, status);
            stmt.setInt(5, selectedTaskIdForEdit);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Zadanie zostało zaktualizowane.");
                zaladujZadaniaUzytkownika();
                clearEditFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zaktualizować zadania.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Błąd podczas aktualizacji zadania: " + e.getMessage());
        }
    }


    private void parseAndPopulateEditFields(Task task) {
        try {
            this.selectedTaskIdForEdit = task.getIdZadania();
            tytulEdycjaField.setText(task.getTytul());
            opisEdycjaArea.setText(task.getOpis());
            dataWykonaniaEdycjaPicker.setValue(task.getDataWykonania());
            statusEdycjaComboBox.getSelectionModel().select(task.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się uzupełnić pól edycji z wybranego zadania.");
            clearEditFields();
        }
    }



    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearAddTaskFields() {
        tytulZadaniaField.clear();
        opisZadaniaArea.clear();
        dataWykonaniaPicker.setValue(null);
    }

    private void clearEditFields() {
        selectedTaskIdForEdit = -1;
        tytulEdycjaField.clear();
        opisEdycjaArea.clear();
        dataWykonaniaEdycjaPicker.setValue(null);
        statusEdycjaComboBox.getSelectionModel().clearSelection();
    }
}