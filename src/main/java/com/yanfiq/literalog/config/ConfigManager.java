package com.yanfiq.literalog.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "com/yanfiq/literalog/config/config.properties";
    private static Path configLocation = Paths.get(System.getProperty("user.home"), ".literalog", "config.properties");
    private static Properties properties;

    static {
        if (! Files.exists(configLocation)) {
            // create directory if needed
            if (! Files.exists(configLocation.getParent())) {
                try {
                    Files.createDirectory(configLocation.getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // extract default config from jar and copy to config location:
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)));
                    BufferedWriter out = Files.newBufferedWriter(configLocation);) {

                in.lines().forEach(line -> {
                    try {
                        out.append(line);
                        out.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException exc) {
                // handle exception, e.g. log and warn user config could not be created
                exc.printStackTrace();
            }
        }

        properties = new Properties();
        try (InputStream input = Files.newInputStream(configLocation)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Unable to load " + configLocation);
            e.printStackTrace();
        }
    }

    public static String getDatabaseEngine() { return properties.getProperty("db.engine"); }
    public static String getServerName() {
        return properties.getProperty("db.serverName");
    }

    public static String getInstanceName() {
        return properties.getProperty("db.instanceName");
    }

    public static int getPort() {
        return Integer.parseInt(properties.getProperty("db.port"));
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static Boolean isAutoConnectEnabled() {
        return Boolean.parseBoolean(properties.getProperty("db.autoConnect"));
    }

    public static String getTheme(){ return properties.getProperty("ui.theme");
    }

    public static String getFontSize(){ return properties.getProperty("ui.fontSize");
    }

    public static void setDatabaseEngine(String dbEngine){
        properties.setProperty("db.engine", dbEngine);
        saveProperties();
    }

    public static void setServerName(String serverName) {
        properties.setProperty("db.serverName", serverName);
        saveProperties();
    }

    public static void setInstanceName(String instanceName) {
        properties.setProperty("db.instanceName", instanceName);
        saveProperties();
    }

    public static void setPort(int port) {
        properties.setProperty("db.port", String.valueOf(port));
        saveProperties();
    }

    public static void setUsername(String username) {
        properties.setProperty("db.username", username);
        saveProperties();
    }

    public static void setPassword(String password) {
        properties.setProperty("db.password", password);
        saveProperties();
    }

    public static void setAutoConnect(boolean bool){
        properties.setProperty("db.autoConnect", String.valueOf(bool));
        saveProperties();
    }

    public static void setTheme(String theme){
        properties.setProperty("ui.theme", theme);
        saveProperties();
    }

    public static void setFontSize(int fontSize){
        properties.setProperty("ui.fontSize", String.valueOf(fontSize));
        saveProperties();
    }

    private static void saveProperties() {
        try (OutputStream output = new FileOutputStream(configLocation.toString())) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}