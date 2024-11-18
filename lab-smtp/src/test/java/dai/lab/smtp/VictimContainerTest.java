package dai.lab.smtp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VictimContainerTest {

    private static final String TEST_FILE_PATH = "src/test/resources/victims.txt";
    private static final String INVALID_FILE_PATH = "invalid_path.txt";
    private VictimContainer victimContainer;

    @BeforeEach
    public void setUp() 
    {
        victimContainer = new VictimContainer(TEST_FILE_PATH);
    }

    // Test de la validation des emails
    @Test
    public void testValidateVictimValidEmails() {
        assertTrue(victimContainer.validateVictim("valid@example.com"));
        assertTrue(victimContainer.validateVictim("user.name@domain.co"));
        assertTrue(victimContainer.validateVictim("user+name@sub.domain.com"));
    }

    @Test
    public void testValidateVictimInvalidEmails() {
        assertFalse(victimContainer.validateVictim("invalid-email.com"));
        assertFalse(victimContainer.validateVictim("another@domain"));
        assertFalse(victimContainer.validateVictim("user@.com"));
        assertFalse(victimContainer.validateVictim("user@domain..com"));
        assertFalse(victimContainer.validateVictim("user@domain.c"));
    }

    // Test pour vérifier que les emails sont chargés correctement depuis le fichier
    @Test
    public void testLoadVictims() {
        assertNotNull(victimContainer.getVictims());
        
        // Vérifier que tous les emails dans la liste sont valides
        for (String victim : victimContainer.getVictims()) {
            assertTrue(victimContainer.validateVictim(victim), "Invalid email found: " + victim);
        }
    }

    // Test pour vérifier que le fichier invalide retourne une liste nulle ou vide
    @Test
    public void testLoadVictimsWithInvalidFile() {
        VictimContainer invalidContainer = new VictimContainer(INVALID_FILE_PATH);
        List<String> victims = invalidContainer.getVictims();
        assertNull(victims, "Victims list should be null when file path is invalid.");
    }

    // Test du constructeur avec un fichier valide
    @Test
    public void testConstructorWithValidFile() {
        VictimContainer container = new VictimContainer(TEST_FILE_PATH);
        assertNotNull(container.getVictims(), "Victim list should not be null after construction.");
    }

    // Test du constructeur avec un fichier vide ou invalide
    @Test
    public void testConstructorWithInvalidFile() {
        VictimContainer container = new VictimContainer(INVALID_FILE_PATH);
        assertNull(container.getVictims(), "Victim list should be null when the file path is invalid.");
    }

    // Test si le fichier est vide
    @Test
    public void testLoadEmptyFile() throws IOException {
        // Crée un fichier temporaire vide pour tester le cas d'un fichier vide
        File tempFile = File.createTempFile("victims", ".txt");
        tempFile.deleteOnExit();
        
        VictimContainer emptyFileContainer = new VictimContainer(tempFile.getAbsolutePath());
        assertTrue(emptyFileContainer.getVictims().isEmpty(), "Victim list should be empty when the file is empty.");
    }
}
