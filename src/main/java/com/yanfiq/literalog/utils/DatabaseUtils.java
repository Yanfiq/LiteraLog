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
import java.util.Objects;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public final class DatabaseUtils {
    private static String dbEngine, serverName, instanceName, username, password;
    private static final ArrayList<String> tablesName = new ArrayList<>(List.of("BOOK", "COLLECTION", "WISHLIST", "BOOKMARK"));
    private static int port;
    private static Connection connection = null;
    private static Path localDB = Paths.get(System.getProperty("user.home"), ".literalog/data", "bookData.db");
    public static SimpleBooleanProperty isConnected = new SimpleBooleanProperty(false);
    public static SimpleBooleanProperty isConnecting = new SimpleBooleanProperty(false);
    private DatabaseUtils(){}

    private static BasicDataSource ds = new BasicDataSource();
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
                                break;
                            }
                        }
                        if (!dbFound) {
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
                        case "BOOK":
                            bookTableAvailable = true;
                            break;
                        case "COLLECTION":
                            colectionTableAvailable = true;
                            break;
                        case "WISHLIST":
                            wishlistTableAvailable = true;
                            break;
                        case "BOOKMARK":
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

    private static boolean executeStatement(String query){
        try{
            Statement statement = connection.createStatement();
            return statement.executeUpdate(query)>0;
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
        return false;
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
        String query = "CREATE TABLE [BOOK](" +
                "[ISBN] VARCHAR(128)," +
                "[Title] VARCHAR(128)," +
                "[Author] VARCHAR(128)," +
                "[TotalPage] INT," +
                "[Publisher] VARCHAR(128)," +
                "[Year] INT," +
                "[Price] INT," +
                "CONSTRAINT PK_BOOK PRIMARY KEY([ISBN])" +
                ");";
        executeStatement(query);
    }

    private static void createTableCollection(){
        String query = "CREATE TABLE [COLLECTION](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_COLLECTION PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_COLLECTION_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOK]([ISBN])," +
                "CONSTRAINT FK_COLLECTION_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");";
        executeStatement(query);
    }

    private static void createTableWishlist(){
        String query = "CREATE TABLE [WISHLIST](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "CONSTRAINT PK_WISHLIST PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_WISHLIST_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOK]([ISBN])," +
                "CONSTRAINT FK_WISHLIST_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
                ");";
        executeStatement(query);
    }

    private static void createTableBookmark(){
        String query = "CREATE TABLE [BOOKMARK](" +
                "[Username] VARCHAR(128)," +
                "[ISBN] VARCHAR(128)," +
                "[LastTimeRead] DATETIME2," +
                "[LastPage] INT," +
                "CONSTRAINT PK_BOOKMARK PRIMARY KEY([Username], [ISBN])," +
                "CONSTRAINT FK_BOOKMARK_BOOK FOREIGN KEY([ISBN]) REFERENCES [BOOK]([ISBN])," +
                "CONSTRAINT FK_BOOKMARK_USER FOREIGN KEY([Username]) REFERENCES [USER]([Name])" +
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
                            Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
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
                            Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
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
            preparedStatement.setString(1, ISBN);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
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

    public static boolean isUsernameAvailable(String username){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name FROM [USER] WHERE Name = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString(1);
                if(name.equals(username)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            DialogUtils.showException(e);
            return false;
        }
    }

    public static boolean isPasswordCorrect(String username, String password){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name FROM [USER] WHERE Name=? AND Password=?;");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString(1);
                if(name.equals(username)){
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            DialogUtils.showException(e);
            return false;
        }
    }

    public static User getUserData(String username, String password){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM [USER] WHERE Name=? AND Password=?;");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (resultSet.next()){
                String _username = null, _password = null;
                LocalDateTime _accountCreated = null;
                int _totalPagesRead = 0;

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    switch (columnName){
                        case "Name": _username = columnValue.toString(); break;
                        case "Password": _password = columnValue.toString(); break;
                        case "TotalPagesRead": _totalPagesRead = Integer.parseInt(columnValue.toString());
                            System.out.println(Integer.parseInt(columnValue.toString())); break;
                        case "AccountCreated": {
                            Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
                            _accountCreated = sqlTimestamp.toLocalDateTime();
                            break;
                        }
                    }
                }
                return new User(_username, _password, _accountCreated, _totalPagesRead);
            }
            return null;
        } catch (SQLException e) {
            DialogUtils.showException(e);
            return null;
        }
    }

    public static void insertUser(User user){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO [USER] (Name, Password, TotalPagesRead, AccountCreated) VALUES (?,?,?,?);");
            preparedStatement.setString(1, user.Username.get());
            preparedStatement.setString(2, user.Password.get());
            preparedStatement.setInt(3, user.totalPagesRead.get());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(user.accountCreated.get()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
        } finally {

        }
    }

    public static void deleteUser(String username){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM [USER] WHERE Name=?;");
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
    }

    public static void updateUser(String username, User user){
        try {
            if(username.equals(user.Username.get())){
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE [USER] SET Password=?, TotalPagesRead=? WHERE Name=?");
                preparedStatement.setString(1, user.Password.get());
                preparedStatement.setInt(2, user.totalPagesRead.get());
                preparedStatement.setString(3, username);
                preparedStatement.executeUpdate();
            }else{
                String old_name = username; String new_name = user.Username.get();

                //get books that's still referenced to the old username
                User.loggedInUser.Username.set(old_name);
                ArrayList<Book> book_collection = getBooks("COLLECTION");
                ArrayList<Book> book_wishlist = getBooks("WISHLIST");
                ArrayList<Book> book_bookmark = getBooks("BOOKMARK");

                //delete books with old reference
                book_wishlist.forEach(book -> deleteBook("WISHLIST", book));
                book_bookmark.forEach(book -> deleteBook("BOOKMARK", book));
                book_collection.forEach(book -> deleteBook("COLLECTION", book));

                //delete coresponding user
                deleteUser(old_name);

                //create new user with new username
                insertUser(user);

                //insert book with new username
                User.loggedInUser.Username.set(new_name);
                book_collection.forEach(book -> insertBook("COLLECTION", book));
                book_wishlist.forEach(book -> insertBook("WISHLIST", book));
                book_bookmark.forEach(book -> insertBook("BOOKMARK", book));
            }
        } catch (SQLException e) {
            DialogUtils.showException(e);
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
    public static void updateBook(String ISBN, Book book){
        if(!Objects.equals(ISBN, book.isbn.get())) return;
        String username = User.loggedInUser.Username.get();
        String title = book.title.get();
        String author = book.author.get();
        String publisher = book.publisher.get();
        int totalPage = book.totalPage.get();
        int year = book.year.get();
        int price = book.price.get();
        int lastPage = book.lastPage.get();
        LocalDateTime lastTimeread = book.lastTimeRead.get();

        try {
            PreparedStatement preparedStatement_book = connection.prepareStatement("UPDATE BOOK SET Title=?, Author=?, Publisher=?, TotalPage=?, Year=?, Price=? WHERE ISBN=?;");
            preparedStatement_book.setString(1, title);
            preparedStatement_book.setString(2, author);
            preparedStatement_book.setString(3, publisher);
            preparedStatement_book.setInt(4, totalPage);
            preparedStatement_book.setInt(5, year);
            preparedStatement_book.setInt(6, price);
            preparedStatement_book.setString(7, ISBN);
            preparedStatement_book.executeUpdate();

            PreparedStatement preparedStatement_bookmark = connection.prepareStatement("UPDATE BOOKMARK SET LastTimeRead=?, LastPage=? WHERE ISBN=?;");
            Timestamp timestamp = Timestamp.valueOf(lastTimeread);
            preparedStatement_bookmark.setTimestamp(1, timestamp);
            preparedStatement_bookmark.setInt(2, lastPage);
            preparedStatement_bookmark.setString(3, ISBN);
            preparedStatement_bookmark.executeUpdate();
        } catch (SQLException e) {
            DialogUtils.showException(e);
        }
    }

    public static void deleteBook(String tableName, Book book){
        String ISBN = book.isbn.get();

        try {
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("DELETE FROM "+tableName+" WHERE ISBN = ? AND Username = ?");
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