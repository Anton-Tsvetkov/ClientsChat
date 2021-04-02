package com.epam.laboratory;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Config {

    private final static Logger LOGGER = Logger.getLogger(Config.class);

    private final static String PATH_TO_SETTINGS = "src/main/resources/settings.properties";
    private final static String PATH_TO_MESSAGES_HISTORY = getStringProperty("PATH_TO_MESSAGES_HISTORY");

    private static int getNumericProperty(String property) {
        try (FileInputStream fileInputStream = new FileInputStream(PATH_TO_SETTINGS)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return Integer.parseInt(properties.getProperty(property));
        } catch (IOException ex) {
            LOGGER.error("Error in file or path: " + PATH_TO_SETTINGS + ex.getMessage());
            ex.printStackTrace();
        }
        return -1;
    }

    private static String getStringProperty(String property) {
        try (FileInputStream fileInputStream = new FileInputStream(PATH_TO_SETTINGS)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties.getProperty(property);
        } catch (IOException ex) {
            LOGGER.error("Error in file or path: " + PATH_TO_SETTINGS + ex.getMessage());
            ex.printStackTrace();
        }
        return "-1";
    }

    public static int getServerPortAddress() {
        return getNumericProperty("SERVER_PORT");
    }

    public static int getMessagesHistoryLimit() {
        return getNumericProperty("MESSAGES_HISTORY_LIMIT");
    }

    public static String getServerIpAddress() {
        return getStringProperty("SERVER_IP_ADDRESS");
    }

    public static String getLeaveFromChatKeyWord() {
        return getStringProperty("QUIT");
    }

    public static String getPathToMessageHistoryFile() {
        return getStringProperty("PATH_TO_MESSAGE_HISTORY");
    }

    public static String getMessagePattern() {
        return getStringProperty("MESSAGE_PATTERN");
    }

    public static void writeMessagesToHistoryFile(Queue<String> messages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_TO_MESSAGES_HISTORY))) {
            for (String message : messages) {
                writer.write(message + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Path error: " + PATH_TO_MESSAGES_HISTORY + " " + e.getMessage());
        }
    }

    public static Queue<String> readMessagesFromHistoryFile() {
        Queue<String> messages = new LinkedBlockingQueue<>(Config.getMessagesHistoryLimit());

        try (BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_MESSAGES_HISTORY))){
            String message;
            while ((message = reader.readLine()) != null) messages.add(message);
        } catch (IOException e) {
            LOGGER.error("Path error: " + PATH_TO_MESSAGES_HISTORY + " " + e.getMessage());
        }
        return messages;
    }

}
