package com.literalog.literalog;

import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public final class AccessDB{
    private static String serverName;
    private static String instanceName;
    private static String port;
    private static String username;
    private static String password;
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

    public static boolean openConnection(String _serverName, String _instanceName, String _port, String _username, String _password){
        serverName = _serverName; instanceName = _instanceName; port = _port; username = _username; password = _password;
        try {
            String dbUrl = "jdbc:sqlserver://"+serverName+"\\"+instanceName+":"+port+";databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            connection = DriverManager.getConnection(dbUrl, username, password);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            connection = null;
            return false;
        }
    }

    public static Connection getConnection(){
        return connection;
    }

    public static ArrayList<Book> getData(String query){
        if(connection == null) return null;
        ArrayList<Book> container = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                String ISBN = null, Title = null, Author = null, Publisher = null;
                int Year = 0, Price = 0, TotalPage = 0, LastPage = 0;
                LocalDateTime LastTimeRead = null;

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    switch (columnName){
                        case "ISBN": ISBN = columnValue.toString(); break;
                        case "Title": Title = columnValue.toString(); break;
                        case "Author": Author = columnValue.toString(); break;
                        case "TotalPage": TotalPage = Integer.parseInt(columnValue.toString()); break;
                        case "Publisher": Publisher = columnValue.toString(); break;
                        case "Year": Year = Integer.parseInt(columnValue.toString()); break;
                        case "Price": Price = Integer.parseInt(columnValue.toString()); break;
                        case "LastPage": LastPage = Integer.parseInt(columnValue.toString()); break;
                        case "LastTimeRead": {
                            java.sql.Timestamp sqlTimestamp = resultSet.getTimestamp("LastTimeRead");
                            LastTimeRead = sqlTimestamp.toLocalDateTime();
                            break;
                        }
                    }
                }
                Book book = new Book(ISBN, Title, Author, TotalPage, Publisher, Year, Price);
                book.lastTimeRead.set(LastTimeRead);
                book.lastPage.set(LastPage);
                container.add(book);
            }
            return container;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void manipulateTable(String query){
        if(connection == null) return;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
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
        return false;
    }
}