package com.projektjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));//ładuje plik FXML
        Scene scene = new Scene(fxmlLoader.load(), 800, 500); //tworzy scene o określonych wymiarach
        stage.setTitle("Logowanie");//wyświetlenie nazwy okienka
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();//uruchamia aplikacje javafx
    }
}