package com.yanfiq.literalog.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "com/yanfiq/literalog/config/config.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Unable to load " + CONFIG_FILE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
