
package com.projektjava;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Task {
    private final IntegerProperty idZadania;
    private final StringProperty tytul;
    private final StringProperty opis;
    private final ObjectProperty<LocalDate> dataWykonania;
    private final StringProperty status;

    public Task(int idZadania, String tytul, String opis, LocalDate dataWykonania, String status) {
        this.idZadania = new SimpleIntegerProperty(idZadania);
        this.tytul = new SimpleStringProperty(tytul);
        this.opis = new SimpleStringProperty(opis);
        this.dataWykonania = new SimpleObjectProperty<>(dataWykonania);
        this.status = new SimpleStringProperty(status);
    }

    // Getters for properties
    public IntegerProperty idZadaniaProperty() {
        return idZadania;
    }

    public StringProperty tytulProperty() {
        return tytul;
    }

    public StringProperty opisProperty() {
        return opis;
    }

    public ObjectProperty<LocalDate> dataWykonaniaProperty() {
        return dataWykonania;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Regular getters (optional, but good practice)
    public int getIdZadania() {
        return idZadania.get();
    }

    public String getTytul() {
        return tytul.get();
    }

    public String getOpis() {
        return opis.get();
    }

    public LocalDate getDataWykonania() {
        return dataWykonania.get();
    }

    public String getStatus() {
        return status.get();
    }
}
