package ch.unil.doplab.recipe.domain;
import org.mindrot.jbcrypt.BCrypt;

public class Utils {

    // Function to hash a plaintext password, borrowed from the StudyBuddy model
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Function to compare a plaintext input to a hashed password, also borrowed from the StudyBuddy model
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
