package com.epam.laboratory.client;

import com.epam.laboratory.Config;
import com.epam.laboratory.network.TCPConnection;
import com.epam.laboratory.network.TCPConnectionListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDRESS = Config.getServerIpAddress();
    private static final int PORT = Config.getServerPortAddress();
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final static Logger LOGGER = Logger.getLogger(ClientWindow.class);


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNIckName = new JTextField("Anon");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false); // запрет редактирования
        log.setLineWrap(true);  // автоматический перенос строк
        fieldInput.addActionListener(this); // делаем отлов нажатия enter

        add(log, BorderLayout.CENTER);  // добавлям в центр поле в которое будем писать
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNIckName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            LOGGER.error("Connection exception: " + e.getMessage());
            printMessage("Connection error.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if (message.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNIckName.getText() + ": " + message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        LOGGER.info("Connection ready...");
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        LOGGER.info("Connection close");
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        LOGGER.error("Connection exception: " + exception.getMessage());
        printMessage("Connection error.");
    }

    private synchronized void printMessage(String message) {
        // гарантирует выполнение в потоке окна
        SwingUtilities.invokeLater(() -> {
            log.append(message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

}
