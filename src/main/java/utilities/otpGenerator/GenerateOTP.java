package utilities.otpGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GenerateOTP {

    public String generateOtp() {
        StringBuilder generatedToken = new StringBuilder();
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            for (int i = 0; i < 4; i++) {
                int randomDigit = number.nextInt(10);
                if (i == 0 && randomDigit == 0) {
                    generatedToken.append(randomDigit + 1);
                } else {
                    generatedToken.append(randomDigit);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedToken.toString();
    }
}