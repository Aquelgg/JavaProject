package com.projektjava;
import com.projektjava.db.DatabaseConnector;
import java.io.IOException;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private TextField loginField; //pole tekstowe do wpisania emailu

    @FXML
    private TextField passwordField; //pole tekstowe do wpisania hasła

    @FXML
    private Label errorLabel; //etykieta wyświetlająca komunikaty pod przyciskiem zaloguj

    @FXML
    private void otworzOknoRejestracji() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/projektjava/register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Rejestracja");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String email = loginField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT * FROM Uzytkownik WHERE email = ? AND haslo = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String imie = rs.getString("imie");
                String emailZBazy = rs.getString("email");
                int id_uzytkownika = rs.getInt("id_uzytkownika");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/projektjava/main-view.fxml"));
                Parent root = loader.load();

                // Przekaż dane do kontrolera głównego
                com.projektjava.MainController mainController = loader.getController();
                mainController.ustawDaneUzytkownika(imie, emailZBazy, id_uzytkownika);

                // Otwórz nowe okno
                Stage stage = new Stage();
                stage.setTitle("Panel Główny");
                stage.setScene(new Scene(root));
                stage.show();

                // Zamknij okno logowania
                Stage currentStage = (Stage) loginField.getScene().getWindow();
                currentStage.close();

            } else {
                errorLabel.setText("Błąd logowania.");
            }

        } catch (SQLException | IOException e) {
            errorLabel.setText("Błąd połączenia.");
            e.printStackTrace();
        }
    }
}