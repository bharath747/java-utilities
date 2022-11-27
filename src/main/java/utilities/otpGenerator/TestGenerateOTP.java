package utilities.otpGenerator;

public class TestGenerateOTP {

    public static void main(String[] args) {
        GenerateOTP generateOTP = new GenerateOTP();
        String otp = generateOTP.generateOtp();
        System.out.print(otp);
    }
}