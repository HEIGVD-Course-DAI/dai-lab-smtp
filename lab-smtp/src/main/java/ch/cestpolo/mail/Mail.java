package ch.cestpolo.mail;

import java.util.List;
import java.util.stream.Collectors;

public class Mail {
    private final Message message;
    private final Person sender;
    private final List<Person> recipients;

    public Mail(Message message, Person sender, List<Person> recipients) {
        this.message = message;
        this.sender = sender;
        this.recipients = recipients;
    }

    public String getBody() {
        return message.getBody();
    }

    public String getSubject() {
        return message.getSubject();
    }

    public String getSender() {
        return sender.getMail();
    }

    public List<String> getRecipients() {
        return recipients.stream()
                .map(Person::getMail)
                .collect(Collectors.toList());
    }

    public String getContentType() {
        return message.getBody().contains("<html>") ? "text/html" : "text/plain";
    }
}
