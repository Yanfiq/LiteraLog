package com.yanfiq.literalog.utils;

import com.yanfiq.literalog.models.Book;
import com.yanfiq.literalog.models.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseUtils {
    private static String dbEngine, serverName, instanceName, username, password;

    private static final ArrayList<String> tablesName = new ArrayList<>(List.of("BOOK", "COLLECTION", "WISHLIST", "BOOKMARK"));
    private static int port;
    private static Connection connection = null;
    private static Path localDB = Paths.get(System.getProperty("user.home"), ".literalog/data", "bookData.db");
    public static SimpleBooleanProperty isConnected = new SimpleBooleanProperty(false);
    public static SimpleBooleanProperty isConnecting = new SimpleBooleanProperty(false);
    private DatabaseUtils(){}
    public static String getServerName() {
        return serverName;
    }
    public static String getInstanceName() {
        return instanceName;
    }
    public static int getPort() {
        return port;
    }
    public static String getUsername() {
        return username;
    }
    public static Task<Void> connectionTask;

    public static void openConnection(String _dbEngine, String _serverName, String _instanceName, int _port, String _username, String _password){
        dbEngine = _dbEngine; serverName = _serverName; instanceName = _instanceName; port = _port; username = _username; password = _password;
        isConnecting.set(true);
        connectionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                switch (dbEngine) {
                    case "SQLite": {
                        if (!Files.exists(localDB)) {
                            // create directory if needed
                            if (!Files.exists(localDB.getParent())) {
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
                    case "MSSQL": {
                        String dbUrl = "jdbc:sqlserver://" + serverName + "\\" + instanceName + ":" + port + ";Encrypt=true;trustServerCertificate=true";
                        connection = DriverManager.getConnection(dbUrl, username, password);
                        boolean dbFound = false;
                        DatabaseMetaData meta = connection.getMetaData();
                        ResultSet rs = meta.getCatalogs();
                        while (rs.next()) {
                            if (rs.getString("TABLE_CAT").equals("LiteraLog")) {
                                dbFound = true;
                            }
                        }
                        if (!dbFound) {
                            System.out.println("Database dibuat");
                            Statement statement1 = connection.createStatement();
                            statement1.executeUpdate("CREATE DATABASE [LiteraLog];");
                        }
                        connection.close();
                        dbUrl = "jdbc:sqlserver://" + serverName + "\\" + instanceName + ":" + port + ";databaseName=LiteraLog;Encrypt=true;trustServerCertificate=true";
                        connection = DriverManager.getConnection(dbUrl, username, password);
                        break;
                    }
                }
                DatabaseMetaData md = connection.getMetaData();
                ResultSet rs = md.getTables(null, null, "%", null);
                boolean bookTableAvailable = false, colectionTableAvailable = false, wishlistTableAvailable = false, bookmarkTableAvailable = false, userTableAvailable = false;
                while (rs.next()) {
                    switch (rs.getString(3)) {
                        case "BOOKS":
                            bookTableAvailable = true;
                            break;
                        case "COLLECTION":
                            colectionTableAvailable = true;
                            break;
                        case "WISHLIST":
                            wishlistTableAvailable = true;
                            break;
                        case "BOOKMARKS":
                            bookmarkTableAvailable = true;
                            break;
                        case "USER":
                            userTableAvailable = true;
                            break;
                    }
                }
                if (!bookTableAvailable) createTableBook();
                if (!colectionTableAvailable) createTableCollection();
                if (!wishlistTableAvailable) createTableWishlist();
                if (!bookmarkTableAvailable) createTableBookmark();
                if (!userTableAvailable) createTableUser();
                return null;
            }
        };

        connectionTask.setOnSucceeded(event -> {
            isConnected.set(true);
            isConnecting.set(false);
        });
        connectionTask.setOnFailed(event -> {
            isConnected.set(false);
            isConnecting.set(false);
        });

        Thread thread = new Thread(connectionTask);
        thread.setDaemon(true);
        thread.start();
    }

    private static void executeStatement(String query){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
    }
    private static void createTableUser(){
        String query = "CREATE TABLE [USER](" +
                "[Name] VARCHAR(128)," +
                "[Password] VARCHAR(128)," +
                "[TotalPagesRead] int," +
                "[AccountCreated] DATETIME2," +
                "CONSTRAINT PK_USER PRIMARY KEY([Name])" +
                ");";
        executeStatement(query);
    }

    private static void createTableBook(){
        String query = "CREATE TABLE [BOOKS](" +
                "[ISBN] VARCHAR(128)," +
                "[Title] VARCHAR(128)," +
                "[Author] VARCHAR(128)," +
                "[TotalPage] INT," +
                "[Publisher] VARCHAR(128)," +
                "[Year] INT," +
                "[Price] INT," +
                "CONSTRAINT PK_BOOKS PRIMARY KEY([ISBN])" +
                ");";
        executeStatement(query);
    }

    private static void createTableCollection(){
        String query = "CREATE TABLE [COLLECTION](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_COLLECTION PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_COLLECTION_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_COLLECTION_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");";
        executeStatement(query);
    }

    private static void createTableWishlist(){
        String query = "CREATE TABLE [WISHLIST](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_WISHLIST PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_WISHLIST_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_WISHLIST_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");";
        executeStatement(query);
    }

    private static void createTableBookmark(){
        String query = "CREATE TABLE [BOOKMARKS](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "[LastTimeRead] DATETIME2," +
                "[LastPage] INT," +
                "CONSTRAINT PK_BOOKMARKS PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_BOOKMARKS_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOKS]([ISBN])," +
                "CONSTRAINT FK_BOOKMARKS_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");";
        executeStatement(query);
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

    public static ArrayList<Book> getBooks(String tableNames){
        String query = "SELECT * FROM BOOK A, "+tableNames+" B WHERE A.ISBN = B.ISBN AND B.Username = ?;";
        ArrayList<Book> container = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, User.loggedInUser.Username.get());

            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                String ISBN = null, Title = null, Author = null, Publisher = null;
                int Year = 0, Price = 0, TotalPage = 0, LastPage = 0;
                LocalDateTime LastTimeRead = null;

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    switch (columnName) {
                        case "ISBN": ISBN = columnValue.toString(); break;
                        case "Title": Title = columnValue.toString(); break;
                        case "Author": Author = columnValue.toString(); break;
                        case "TotalPage": TotalPage = Integer.parseInt(columnValue.toString()); break;
                        case "Publisher": Publisher = columnValue.toString(); break;
                        case "Year": Year = Integer.parseInt(columnValue.toString()); break;
                        case "Price": Price = Integer.parseInt(columnValue.toString()); break;
                        case "LastPage": LastPage = Integer.parseInt(columnValue.toString()); break;
                        case "LastTimeRead": {
                            java.sql.Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
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
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
        return container;
    }

    public static ArrayList<Book> getBooks(String tableNames, String keyword){
        //structuring query
        String query = "SELECT * " +
                "FROM [BOOK] A, "+ tableNames + " B " +
                "WHERE A.ISBN = B.ISBN AND " +
                "B.Username = ? AND " +
                "A.ISBN + A.Title + A.Author LIKE '%"+keyword+"%';";

        ArrayList<Book> container = new ArrayList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, User.loggedInUser.Username.get());
//            preparedStatement.setString(2, keyword);

            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                String ISBN = null, Title = null, Author = null, Publisher = null;
                int Year = 0, Price = 0, TotalPage = 0, LastPage = 0;
                LocalDateTime LastTimeRead = null;

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    switch (columnName) {
                        case "ISBN": ISBN = columnValue.toString(); break;
                        case "Title": Title = columnValue.toString(); break;
                        case "Author": Author = columnValue.toString(); break;
                        case "TotalPage": TotalPage = Integer.parseInt(columnValue.toString()); break;
                        case "Publisher": Publisher = columnValue.toString(); break;
                        case "Year": Year = Integer.parseInt(columnValue.toString()); break;
                        case "Price": Price = Integer.parseInt(columnValue.toString()); break;
                        case "LastPage": LastPage = Integer.parseInt(columnValue.toString()); break;
                        case "LastTimeRead": {
                            java.sql.Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
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
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
        return container;
    }

    private static boolean isInRecord(String ISBN){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ISBN FROM BOOK WHERE ISBN = ?");
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next())count++;
            return count > 0;
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
        return false;
    }

    private static ArrayList<String> getColumn(String tableName){
        if(connection == null) return null;
        ArrayList<String> container = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'?';");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Object columnValue = resultSet.getObject(1);
                container.add(columnValue.toString());
            }
            return container;
        } catch (Exception e) {
            DialogUtils.showException(e);
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

    public static void insertBook(String tableName, Book book){
        String username = User.loggedInUser.Username.get();
        String ISBN = book.isbn.get();
        String title = book.title.get();
        String author = book.author.get();
        String publisher = book.publisher.get();
        int totalPage = book.totalPage.get();
        int year = book.year.get();
        int price = book.price.get();
        int lastPage = book.lastPage.get();
        LocalDateTime lastTimeread = book.lastTimeRead.get();

        try {
            if(!isInRecord(ISBN)){
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOK (ISBN, Title, Author, Publisher, TotalPage, Year, Price) VALUES (?,?,?,?,?,?,?)");
                preparedStatement.setString(1, ISBN);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, author);
                preparedStatement.setString(4, publisher);
                preparedStatement.setInt(5, totalPage);
                preparedStatement.setInt(6, year);
                preparedStatement.setInt(7, price);
                preparedStatement.executeUpdate();
            }
            PreparedStatement preparedStatement;
            if(tableName.equals("BOOKMARK")){
                preparedStatement = connection.prepareStatement("INSERT INTO "+tableName+" (Username, ISBN, LastTimeRead, LastPage) VALUES (?,?,?,?)");

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, ISBN);
                Timestamp timestamp = Timestamp.valueOf(lastTimeread);
                preparedStatement.setTimestamp(3, timestamp);
                preparedStatement.setInt(4, lastPage);
            }else{
                preparedStatement = connection.prepareStatement("INSERT INTO "+tableName+" (Username, ISBN) VALUES (?,?)");

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, ISBN);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
    }

    //currently just for bookmarks table
    public static void updateBook(String ISBN, String propertyName, String newValue){
        try {
            PreparedStatement preparedStatement;
            String tableName = null;
            for(String i : tablesName){
                ArrayList<String> columnName = getColumn(i);
                for(String j : columnName){
                    if(j.equals(propertyName)){
                        tableName = i;
                        break;
                    }
                }
                if(tableName!=null)break;
            }
            preparedStatement = connection.prepareStatement("UPDATE "+tableName+" A SET "+propertyName+" = ? WHERE Username=? AND A.ISBN=?;");
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, User.loggedInUser.Username.get());
            preparedStatement.setString(3, ISBN);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
    }

    public static void deleteBook(String tableName, Book book){
        String ISBN = book.isbn.get();

        try {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("DELETE FROM "+tableName+" A WHERE A.ISBN = ? AND A.Username = ?");
            preparedStatement.setString(1, ISBN);
            preparedStatement.setString(2, User.loggedInUser.Username.get());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
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