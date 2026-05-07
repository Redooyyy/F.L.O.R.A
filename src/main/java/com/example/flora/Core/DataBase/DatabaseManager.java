package com.example.flora.Core.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
   private static String url = "jdbc:mysql://localhost:3306/floraDB";
   private static String user = "root";
   private static String password = "20052005";
   private static DatabaseManager databaseManager; //self referential
   private Connection connection;

private DatabaseManager() throws SQLException {
   connect();
}

   public static synchronized DatabaseManager getDatabaseManager() throws SQLException {
      if (databaseManager == null) {
         databaseManager = new DatabaseManager();
      }
      return databaseManager;
   }


   private void connect() throws SQLException{
        this.connection = DriverManager.getConnection(url,user,password);
   }

   public Connection getConnection() {
      return connection;
   }
}
