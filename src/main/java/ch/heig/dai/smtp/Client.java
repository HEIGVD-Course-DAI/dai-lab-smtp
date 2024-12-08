package ch.heig.dai.smtp;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {

        final String EOL = "\r\n";
        String smtpServer = "localhost"; // Adresse du serveur SMTP
        int port = 1025; // Port du serveur SMTP
        String sender = "shakira@fakeemail.com"; // Expéditeur
        String recipient = "victim@example.com"; // Destinataire
        String bcc = "hidden@example.com"; // Destinataire caché
        String subject = "You've been pranked!";
        String message = "This is a prank email. Just for fun!";

        try (Socket socket = new Socket(smtpServer, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Lire la réponse du serveur SMTP
            System.out.println("Server: " + reader.readLine());

            // Envoyer les commandes SMTP
            writer.write("HELO localhost" + EOL);
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            writer.write("MAIL FROM:<" + sender + ">" + EOL);
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            writer.write("RCPT TO:<" + recipient  + ">" + EOL);
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            writer.write("RCPT TO:<" + bcc + ">" + EOL); // Ajout d'un destinataire caché
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            writer.write("DATA" + EOL);
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            // Rédiger l'email avec des en-têtes complets
            writer.write("From: " + sender + EOL);
            writer.write("To: " + recipient + EOL);
            writer.write("Bcc: " + bcc + EOL);
            writer.write("Subject: " + subject + EOL);
            writer.write(EOL); // Séparation des en-têtes et du corps
            writer.write(message + EOL);
            writer.write("." + EOL); // Fin du message
            writer.flush();
            System.out.println("Server: " + reader.readLine());

            writer.write("QUIT" + EOL);
            writer.flush();
            System.out.println("Server: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
