package dai.lab.smtp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class VictimContainer {
    private ArrayList<String> victims;

    /*
    Constructor to initialize the VictimContainer with a list of victims loaded from a file.
    @param filePath : the path of the file containing victim email addresses.
    */
    public VictimContainer(String filePath) {
        this.victims = this.loadVictim(filePath);
    }

    /*
    Getter for the list of victims.
    @return : the list of valid victim email addresses.
    */
    public ArrayList<String> getVictims() {
        return this.victims;
    }

    /*
    Validates the format of an email address.
    @param userMail : the email address to validate.
    @return : true if the email is valid, false otherwise.
    */
    public boolean validateVictim(String userMail) {
        if (userMail == null || userMail.isEmpty()) {
            return false;
        }
        
        // Expression régulière pour valider les emails
        String emailRegex = "^[\\w._%+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$";
    
        // Retourne vrai si l'email correspond à l'expression régulière, sinon faux
        return userMail.matches(emailRegex);
    }
    

    /*
    Loads a list of valid email addresses from a file.
    @param filePath : the path of the file containing victim email addresses.
    @return : a list of valid victim email addresses or null if the file path is invalid.
    */
    public ArrayList<String> loadVictim(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
    
        ArrayList<String> victims = new ArrayList<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String userMail = reader.readLine();
    
            while (userMail != null) {
                if (this.validateVictim(userMail)) {
                    System.out.print(userMail);
                    victims.add(userMail);
                }
                userMail = reader.readLine();
            }
        } catch (Exception e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return null;
        }
        return victims;
    }
    
}
