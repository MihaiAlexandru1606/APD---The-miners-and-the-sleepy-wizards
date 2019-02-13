import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Niculescu Mihai Alexandru 335CB
 * @file : Miner.java
 * Class for a miner.
 */
public class Miner extends Thread {
    private HashSet<Integer> solved;
    private int hashCount;
    private CommunicationChannel channel;

    /**
     * Creates a {@code Miner} object.
     *
     * @param hashCount number of times that a miner repeats the hash operation when
     *                  solving a puzzle.
     * @param solved    set containing the IDs of the solved rooms
     * @param channel   communication channel between the miners and the wizards
     */
    public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
        this.solved = (HashSet<Integer>) solved;
        this.hashCount = hashCount;
        this.channel = channel;
    }


    /**
     * functia care caculeaza hash-ul din clasa slover.Main
     *
     * @param input string care urmeaza sa fie prelucrat
     * @return hash
     */
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


    @Override
    public void run() {
        while (true) {

            /** mesajul primit de la vrajitor */
            Message messageFromWizard = channel.getMessageWizardChannel();
            if (messageFromWizard == null)
                continue;

            /** mesajul care trebuie sa fie prelucrat */
            String data = messageFromWizard.getData();
            /** "camera parinte " */
            int root = messageFromWizard.getParentRoom();
            /** camera curenta in care se afla minerul */
            int node = messageFromWizard.getCurrentRoom();

            /** daca am primit mesajul de exit, terminarea programului */
            if (data.equals(Wizard.EXIT)) {
                return;
            }

            /** adaugam camera curenta in hashSet cu camere rezolvate */
            synchronized (solved) {
                if (solved.contains(node)) {
                    continue;
                }
                solved.add(node);
            }

            /** prelucarea mesajului */
            String hash = data;
            for (int i = 0; i < hashCount; i++) {
                hash = encryptThisString(hash);
            }
            /** compunerea si trimiterea cu rezolvarea catre vrajitor */
            Message messageToWizard = new Message(root, node, hash);
            channel.putMessageMinerChannel(messageToWizard);
        }
    }
}
