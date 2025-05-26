package com.projektjava;

import com.projektjava.db.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class MainController {

    @FXML
    private ListView<String> zadaniaListView;


    @FXML
    private Label imieLabel;

    @FXML
    private Label emailLabel;

    public void ustawDaneUzytkownika(String imie, String email, int id_uzytkownika) {
        imieLabel.setText("Imię: " + imie);
        emailLabel.setText("Email: " + email);

        ObservableList<String> listaZadan = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT pz.id_zadania, z.tytul, z.opis, z.data_wykonania, z.status " +
                    "FROM PrzypisanieZadania pz JOIN Zadanie z ON pz.id_zadania = z.id_zadania " +
                    "WHERE pz.id_uzytkownika = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id_uzytkownika);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String zadanie = "ID: " + rs.getInt("id_zadania") + ", Tytuł: " + rs.getString("tytul") +
                        ", Opis: " + rs.getString("opis") + ", Data wykonania: " + rs.getDate("data_wykonania") +
                        ", Status: " + rs.getString("status");
                listaZadan.add(zadanie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        zadaniaListView.setItems(listaZadan);
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
        }
    }
}