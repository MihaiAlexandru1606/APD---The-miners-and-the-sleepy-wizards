import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage :  java pathToFile numberRoom numberOfHashes");
            System.exit(1);
        }
        int numberNode = Integer.parseInt(args[1]);
        int numberHash = Integer.parseInt(args[2]);
        BufferedWriter data = new BufferedWriter(new FileWriter(args[0] + numberNode + "_data.txt"));
        BufferedWriter graph = new BufferedWriter(new FileWriter(args[0] + numberNode + "_graph.txt"));
        BufferedWriter answer = new BufferedWriter(new FileWriter(args[0] + numberNode + "_answer.txt"));
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder buffer = new StringBuilder("");
        for (int i = 0; i < numberNode; i++) {
            for (int j = 0; j < 32; j++) {
                if (j == 8 || j == 12 || j == 16 || j == 24) {
                    buffer.append('-');
                    continue;
                }
                buffer.append(secureRandom.nextInt(10));
            }
            String hash = multiple(buffer.toString(), numberHash);
            data.write(buffer.toString() + "\n");
            answer.write(hash + "\n");
            buffer.setLength(0);
        }

        for (int i = 0; i < numberNode; i++) {
            for (int j = 0; j < numberNode; j++) {
                if (j == numberNode - 1) {
                    graph.write((secureRandom.nextInt() % 2) + "\n");
                    continue;
                }
                graph.write(Math.abs((secureRandom.nextInt() % 2)) + ", ");
            }
        }

        data.close();
        graph.close();
        answer.close();
    }

    private static String multiple(String input, int numberHash) {
        String hash = input;

        for (int i = 0; i < numberHash; i++) {
            hash = encryptThisString(hash);
        }

        return hash;
    }

    private static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // convert to string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xff & messageDigest[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
