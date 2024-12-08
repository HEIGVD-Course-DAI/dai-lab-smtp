package ch.cestpolo.config;

import ch.cestpolo.mail.Message;
import ch.cestpolo.mail.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class Config {
    private final static String MAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final static Pattern MAIL_PATTERN = Pattern.compile(MAIL_REGEX);
    private final static Path PERSON_PATH = Path.of("./config/persons.txt"),
            CONFIG_PATH = Path.of("./config/config.properties"),
            MESSAGE_PATH = Path.of("./config/messages.txt");
    private String serverAddress;
    private int serverPort;
    private int numberGroups;
    private final List<Person> persons;
    private final List<Message> messages;

    public Config() {
        try {
            loadConfig();
            persons = loadPersons();
            messages = loadMessages();
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    private void loadConfig() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);
            serverAddress = properties.getProperty("serverAddress");
            serverPort = Integer.parseInt(properties.getProperty("serverPort"));
            numberGroups = Integer.parseInt(properties.getProperty("numberGroups"));
        }
    }

    private List<Message> loadMessages() throws IOException {
        List<Message> messages = new LinkedList<>();
        try (BufferedReader reader = Files.newBufferedReader(MESSAGE_PATH, StandardCharsets.UTF_8)) {
            String line;
            String subject = null;
            StringBuilder body = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("subject:")) {
                    subject = line.substring("subject:".length()).trim();
                } else if (line.startsWith("body:")) {
                    body.append(line.substring("body:".length()).trim()).append("\n");
                } else if (line.startsWith("END")) {
                    if (subject != null) {
                        messages.add(new Message(subject, body.toString().trim()));
                    }
                    subject = null;
                    body.setLength(0);
                } else {
                    body.append(line).append("\n");
                }
            }
        }
        if (messages.isEmpty()) throw new RuntimeException("No messages found");
        return messages;
    }

    private List<Person> loadPersons() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(PERSON_PATH, StandardCharsets.UTF_8)) {
            List<Person> persons = new LinkedList<>(reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .peek(line -> {
                        if (!isMail(line)) {
                            throw new RuntimeException("Invalid mail: " + line);
                        }
                    })
                    .map(Person::new)
                    .toList());
            if (persons.size() < 3) {
                throw new RuntimeException("Need at least 3 persons, got " + persons.size());
            }
            Collections.shuffle(persons);
            return persons;
        }
    }

    private boolean isMail(String mail) {
        return MAIL_PATTERN.matcher(mail).matches();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNumberGroups() {
        return numberGroups;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
