package com.literalog.literalog;

import java.sql.*;

public class AccessDB{
    private static String dbUrl = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";
    private static String username = "nerd";
    private static String password = "noreadnolife";
    private static Connection connection = null;
    private static String user_role = null;

    public static boolean openConnection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            connection = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connected to the database!");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAccessGranted(String username, String password){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM [USER]");
            while(resultSet.next()){
                if(username.equals(resultSet.getString("Name")) && password.equals(resultSet.getString("Password"))){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Connection getConnection(){
        return connection;
    }

    public static void displayData(String query){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Processing data
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.print(String.format("|%-25s|", columnName));
            }
            System.out.println();
            String border = new String(new char[27*columnCount]).replace("\0", "-");
            System.out.println(border);
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object columnValue = resultSet.getObject(i);

                    System.out.print(String.format("|%-25s|", columnValue));
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean closeConnection(){
        if(connection!=null){
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Connection closed");
        return false;
    }

    public static void main(String[] args){
        openConnection();
    }
}