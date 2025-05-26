package com.projektjava;

import com.projektjava.db.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField registerNameField;

    @FXML
    private TextField registerEmailField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private Label rejestracjaWiadomoscLabel;

    @FXML
    private void obsluzRejestracje() {
        String imie = registerNameField.getText();
        String email = registerEmailField.getText();
        String haslo = registerPasswordField.getText();

        if (imie.isEmpty() || email.isEmpty() || haslo.isEmpty()) {
            rejestracjaWiadomoscLabel.setText("Wszystkie pola są wymagane.");
            return;
        }

        try (Connection conn = DatabaseConnector.connect()) {
            String insertQuery = "INSERT INTO Uzytkownik (imie, email, haslo) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, imie);
            stmt.setString(2, email);
            stmt.setString(3, haslo);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                rejestracjaWiadomoscLabel.setText("Rejestracja udana!");
                // Opcjonalnie: wyczyść pola po sukcesie
                registerNameField.clear();
                registerEmailField.clear();
                registerPasswordField.clear();
            } else {
                rejestracjaWiadomoscLabel.setText("Nie udało się zarejestrować.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            rejestracjaWiadomoscLabel.setText("Błąd bazy danych.");
        }
    }
}
