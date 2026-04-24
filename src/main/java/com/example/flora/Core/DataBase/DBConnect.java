package com.example.flora.Core.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
   private static String url = "jdbc:mysql://localhost:3306/floraDB";
   private static String user = "root";
   private static String password = "20052005";

   public static Connection getConnect() throws SQLException{
       return DriverManager.getConnection(url,user,password);
   }
}
