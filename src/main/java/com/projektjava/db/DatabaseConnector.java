package com.projektjava.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/JavaProjekt"; //URL połączenia JDBC do lokalnej bazy pgAdmin4
    private static final String USER = "postgres"; //nazwa użytkownika
    private static final String PASSWORD = "postgres"; //hasło użytkownika

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);//użycie DriverMenager do wywołania bazy danych i połączenia się z nią
    }
}
