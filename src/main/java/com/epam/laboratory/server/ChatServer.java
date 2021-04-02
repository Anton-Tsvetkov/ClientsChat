package com.epam.laboratory.server;

import com.epam.laboratory.Config;
import com.epam.laboratory.network.TCPConnection;
import com.epam.laboratory.network.TCPConnectionListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ChatServer implements TCPConnectionListener {


    private final Queue<String> LAST_FIVE_MESSAGES;


    public static void main(String[] args) {
        new ChatServer();
    }

    private final List<TCPConnection> connections = new ArrayList<>();
    private final static org.apache.log4j.Logger LOGGER = Logger.getLogger(ChatServer.class);


    private ChatServer() {
        LOGGER.info("Server running...");

        LAST_FIVE_MESSAGES = loadMessagesHistory();

        try (ServerSocket serverSocket = new ServerSocket(Config.getServerPortAddress())) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    LOGGER.error("TCPConnection exception: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);  // роняем приложение
        }

    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        for (TCPConnection connection : connections) {
            connection.sendString(value);
        }
    }

    private void sendToOnlyConnection(TCPConnection tcpConnection, String value) {
        tcpConnection.sendString(value);
    }

    private void saveMessagesHistory(Queue<String> lastFiveMessages){
        Config.writeMessagesToHistoryFile(lastFiveMessages);
    }

    private Queue<String> loadMessagesHistory(){
        return Config.readMessagesFromHistoryFile();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        LOGGER.info("Client connected: " + tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
        for (String message :
                LAST_FIVE_MESSAGES) {
            sendToOnlyConnection(tcpConnection, message);
        }
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        LAST_FIVE_MESSAGES.remove();
        LAST_FIVE_MESSAGES.add(value);
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        LOGGER.info("Client disconnected: " + tcpConnection);
        saveMessagesHistory(LAST_FIVE_MESSAGES);
        sendToAllConnections("Client disconnected: " + tcpConnection);

    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        LOGGER.error("TCPConnection exception: " + exception.getMessage());
    }
}
