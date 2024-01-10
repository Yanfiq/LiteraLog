package com.literalog.literalog;

import java.sql.*;

public final class AccessDB{
    private static String serverName = "localhost";
    private static String instanceName = "MSSQLSERVER";
    private static String port = "1433";
    private static String username = "nerd";
    private static String password = "noreadnolife";
    private static Connection connection = null;
    private AccessDB(){}
    public static String getServerName() {
        return serverName;
    }

    public static String getInstanceName() {
        return instanceName;
    }

    public static String getPort() {
        return port;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean openConnection(String serverName, String instanceName, String port, String username, String password){
        try {
            String dbUrl = "jdbc:sqlserver://"+serverName+"\\"+instanceName+":"+port+";databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            connection = DriverManager.getConnection(dbUrl, username, password);
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
}