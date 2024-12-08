package ch.cestpolo.mail;

import java.util.Collections;
import java.util.List;

public class Group {
    private static int number = 0;
    private final List<Person> recipients;
    private final Person sender;

    public Group(List<Person> persons, int size) {
        if (persons.size() < size) throw new IllegalArgumentException("Group size does not match");
        if (size < 3) throw new IllegalArgumentException("Group size must be at least 3");
        int offset = size * number;
        List<Person> recipientsCopy = List.copyOf(persons); // Copy the list to avoid modifying the original list
        sender = recipientsCopy.get(offset); // The sender is the first person in the group
        recipients = recipientsCopy.subList(1 + offset, size + offset); // The recipients are the rest of the group
        number++;
    }

    public Mail create(List<Message> messages) {
        Collections.shuffle(messages);
        return new Mail(messages.getFirst(), sender, recipients);
    }
}
