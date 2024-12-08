package ch.cestpolo.smtp;

import ch.cestpolo.mail.Mail;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private final static Logger LOG = Logger.getLogger(Client.class.getName());
    private final static String CRLF = "\r\n";

    public void connect(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            LOG.info("Connected to server: " + serverAddress + ":" + serverPort);
        } catch (UnknownHostException e) {
            String error = "Unknown host: " + serverAddress;
            LOG.severe(error);
            throw new RuntimeException(error, e);
        } catch (IOException e) {
            String error = "Error connecting to server: " + serverAddress + ":" + serverPort;
            LOG.severe(error);
            throw new RuntimeException(error, e);
        }
    }

    public void send(Mail mail) {
        try {
            String response = in.readLine();
            LOG.info("Received: " + response);
            sendCommand("EHLO localhost");
            sendCommand("MAIL FROM: <" + mail.getSender() + ">");
            for (String recipient : mail.getRecipients()) sendCommand("RCPT TO: <" + recipient + ">");
            sendCommand("DATA");
            sendMessageBody(mail);
            sendCommand(".");
            response = in.readLine();
            LOG.info("Received: " + response);
            LOG.info("Mail sent successfully");
        } catch (IOException e) {
            String error = "Error sending mail: " + e.getMessage();
            LOG.severe(error);
            throw new RuntimeException(error, e);
        }
    }

    private void sendMessageBody(Mail mail) throws IOException {
        String encodedSubject = Base64.getEncoder().encodeToString(mail.getSubject().getBytes(StandardCharsets.UTF_8));
        StringBuilder message = new StringBuilder()
                .append("Content-Type: ").append(mail.getContentType()).append("; charset=utf-8").append(CRLF)
                .append("FROM: ").append(mail.getSender()).append(CRLF)
                .append("TO: ").append(String.join(", ", mail.getRecipients())).append(CRLF)
                .append("Subject: =?utf-8?B?").append(encodedSubject).append("?=").append(CRLF)
                .append(CRLF)
                .append(mail.getBody()).append(CRLF)
                .append(".");
        write(message.toString());
    }

    private void sendCommand(String command) throws IOException {
        write(command);
    }

    private void write(String message) throws IOException {
        out.write(message + CRLF);
        out.flush();
        readResponse();
        LOG.info("Sent: " + message);
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                sendCommand("QUIT");
                in.close();
                out.close();
                socket.close();
                LOG.info("Connection closed");
            }
        } catch (IOException e) {
            String error = "Error closing connection: " + e.getMessage();
            LOG.severe(error);
            LOG.severe(error + e.getMessage());
        }
    }

    private void readResponse() throws IOException {
        String response = in.readLine();
        if (!response.startsWith("250") && !response.startsWith("220")) {
            throw new RuntimeException("Error: " + response);
        }
        LOG.info("Received: " + response);
    }
}