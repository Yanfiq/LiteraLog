package com.yanfiq.literalog.utils;

import com.yanfiq.literalog.config.ConfigManager;
import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public final class DatabaseUtils {
    private static String dbEngine, serverName, instanceName, port, username, password;
    private static Connection connection = null;
    private static Path localDB = Paths.get(System.getProperty("user.home"), ".literalog/data", "bookData.db");
    public static SimpleBooleanProperty isConnected = new SimpleBooleanProperty(false);
    private DatabaseUtils(){}
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

    public static boolean openConnection(String _dbEngine, String _serverName, String _instanceName, String _port, String _username, String _password){
        dbEngine = _dbEngine; serverName = _serverName; instanceName = _instanceName; port = _port; username = _username; password = _password;
        try {
            switch (dbEngine){
                case "SQLite":
                {
                    if (! Files.exists(localDB)) {
                        // create directory if needed
                        if (! Files.exists(localDB.getParent())) {
                            try {
                                Files.createDirectory(localDB.getParent());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    String dbUrl = "jdbc:sqlite:" + localDB;
                    connection = DriverManager.getConnection(dbUrl);
                    break;
                }
                case "MSSQL":
                {
                    String dbUrl = "jdbc:sqlserver://"+serverName+"\\"+instanceName+":"+port+";Encrypt=true;trustServerCertificate=true";
                    connection = DriverManager.getConnection(dbUrl, username, password);
//                    Statement statement = connection.createStatement();
//                    ResultSet rs = statement.executeQuery("SELECT name FROM sys.databases;");
                    boolean dbFound = false;
                    DatabaseMetaData meta = connection.getMetaData();
                    ResultSet rs = meta.getCatalogs();
                    while(rs.next()){
                        if(rs.getString("TABLE_CAT").equals("LiteraLog")){
                            dbFound = true;
                        }
                    }
                    if(!dbFound){
                        System.out.println("Database dibuat");
                        Statement statement1 = connection.createStatement();
                        statement1.executeUpdate("CREATE DATABASE [LiteraLog];");
                    }
                    connection.close();
                    dbUrl = "jdbc:sqlserver://"+serverName+"\\"+instanceName+":"+port+";databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";
                    connection = DriverManager.getConnection(dbUrl, username, password);
                    break;
                }
            }
            isConnected.set(true);
            DatabaseMetaData md = connection.getMetaData();
            boolean bookTable = false, collectionTable = false, bookmarkTable = false, wishlistTable = false, userTable = false;
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                switch (rs.getString(3)){
                    case "BOOKS": bookTable = true; break;
                    case "COLLECTION": collectionTable = true; break;
                    case "WISHLIST": wishlistTable = true; break;
                    case "BOOKMARK": bookmarkTable = true; break;
                    case "USER": userTable = true; break;
                }
                System.out.println(rs.getString(3));
            }
            if(!(bookTable && collectionTable && wishlistTable && bookmarkTable && userTable)) createTable();
            return true;
        } catch (SQLException e) {
            connection = null;
            isConnected.set(false);
            return false;
        }
    }
    private static void createTable(){
        System.out.println("tabel dibuat");
        String[] queries = {"CREATE TABLE [USER](" +
                "[Name] VARCHAR(128)," +
                "[Password] VARCHAR(128)," +
                "[TotalPagesRead] int," +
                "[AccountCreated] DATETIME2," +
                "CONSTRAINT PK_USER PRIMARY KEY([Name])" +
                ");",
                "CREATE TABLE [BOOKS](" +
                "[ISBN] VARCHAR(128)," +
                "[Title] VARCHAR(128)," +
                "[Author] VARCHAR(128)," +
                "[TotalPage] INT," +
                "[Publisher] VARCHAR(128)," +
                "[Year] INT," +
                "[Price] INT," +
                "CONSTRAINT PK_BOOKS PRIMARY KEY([ISBN])" +
                ");",
                "CREATE TABLE [COLLECTION](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_COLLECTION PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_COLLECTION_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_COLLECTION_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");",
                "CREATE TABLE [WISHLIST](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_WISHLIST PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_WISHLIST_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_WISHLIST_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");",
                "CREATE TABLE [BOOKMARKS](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "[LastTimeRead] DATETIME2," +
                "[LastPage] INT," +
                "CONSTRAINT PK_BOOKMARKS PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_BOOKMARKS_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_BOOKMARKS_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");"};
        for(String it : queries){
            try {
                PreparedStatement statement = connection.prepareStatement(it);
                statement.execute();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private static void resetTable() throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            String tableName = rs.getString(3);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE ["+tableName+"];");
        }
    }

    public static ArrayList<Book> getBooksData(String query){
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

    public static ArrayList<User> getUsersData(){
        if(connection == null) return null;
        ArrayList<User> container = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM [USER]");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                String _username = null, _password = null;
                LocalDateTime _accountCreated = LocalDateTime.now();
                int _totalPagesRead = 0;

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    switch (columnName){
                        case "Name": _username = columnValue.toString(); break;
                        case "Password": _password = columnValue.toString(); break;
                        case "TotalPagesRead": _totalPagesRead = Integer.parseInt(columnValue.toString()); break;
                        case "AccountCreated": {
                            java.sql.Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
                            _accountCreated = sqlTimestamp.toLocalDateTime();
                            break;
                        }
                    }
                }
                User user = new User(_username, _password, _accountCreated, _totalPagesRead);
                container.add(user);
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
        isConnected.set(false);
        return false;
    }
}