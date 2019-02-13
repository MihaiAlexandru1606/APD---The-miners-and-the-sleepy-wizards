import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 * Niculescu Mihai Alexandru 335CB
 *
 * @file : CommunicationChannel.java
 */
public class CommunicationChannel {
    /**
     * buffer-ul care retine mesajele primite de la miner catre vrajitor
     */
    private ArrayBlockingQueue<Message> bufferMiner;
    /**
     * buffer-ul care retine mesajele de la vrajitor la miner
     */
    private ArrayBlockingQueue<Message> bufferWizard;
    /**
     * capitatea buffer-ului
     */
    private final int CAPACITY = 1000;

    private ConcurrentHashMap<Long, Integer> parents;

    /**
     * Creates a {@code CommunicationChannel} object.
     */
    public CommunicationChannel() {
        bufferMiner = new ArrayBlockingQueue<>(CAPACITY);
        bufferWizard = new ArrayBlockingQueue<>(CAPACITY);
        parents = new ConcurrentHashMap<>();
    }

    /**
     * Puts a message on the miner channel (i.e., where miners write to and wizards
     * read from).
     *
     * @param message message to be put on the channel
     */
    public void putMessageMinerChannel(Message message) {
        try {
            bufferMiner.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets a message from the miner channel (i.e., where miners write to and
     * wizards read from).
     *
     * @return message from the miner channel
     */
    public Message getMessageMinerChannel() {
        try {
            return bufferMiner.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Puts a message on the wizard channel (i.e., where wizards write to and miners
     * read from).
     *
     * @param message message to be put on the channel
     */
    public void putMessageWizardChannel(Message message) {
        if(message.getData().equals(Wizard.EXIT)){
            try {
                bufferWizard.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

        if(message.getData().equals(Wizard.END)){
            return;
        }

        Integer parent = parents.putIfAbsent(Thread.currentThread().getId(), message.getCurrentRoom());

        if(parent != null){
            message.setParentRoom(parent);

            try {
                bufferWizard.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            parents.remove(Thread.currentThread().getId());
        }

    }

    /**
     * Gets a message from the wizard channel (i.e., where wizards write to and
     * miners read from).
     *
     * @return message from the miner channel
     */
    public Message getMessageWizardChannel() {
        try {
            return bufferWizard.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
