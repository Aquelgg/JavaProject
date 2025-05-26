package com.projektjava;
import com.projektjava.db.DatabaseConnector;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private TextField loginField; //pole tekstowe do wpisania emailu

    @FXML
    private TextField passwordField; //pole tekstowe do wpisania hasła

    @FXML
    private Label errorLabel; //etykieta wyświetlająca komunikaty pod przyciskiem zaloguj

    @FXML
    private void handleLogin() { //ręczne logowanie do bazy danych(okienko)
        String email = loginField.getText();
        String password = passwordField.getText();//te obie komedy pobierają wartości wpisane przez użytkownika

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT * FROM Uzytkownik WHERE email = ? AND haslo = ?";//zapytanie sprawdzające czy istnieje użytkownik o podanych haśle
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery(); //wykonanie zadania
            if (rs.next()) {
                errorLabel.setText("Zalogowano!");
            } else {
                errorLabel.setText("Błąd logowania.");
            }

        } catch (SQLException e) {
            errorLabel.setText("Błąd połączenia.");
            e.printStackTrace();
        }
    }
}
