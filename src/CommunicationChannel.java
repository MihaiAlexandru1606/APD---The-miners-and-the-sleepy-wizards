import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 * Niculescu Mihai Alexandru 335CB
 * @file : CommunicationChannel.java
 */
public class CommunicationChannel {
    /** buffer-ul care retine mesajele primite de la miner catre vrajitor */
    private ArrayBlockingQueue<Message> bufferMiner;
    /** buffer-ul care retine mesajele de la vrajitor la miner  */
    private ArrayBlockingQueue<Message> bufferWizard;
    /** capitatea buffer-ului  */
    private final int CAPACITY = 1000;
    /** retine id thread-ului "Wizard" care trimite mesajul */
    private AtomicLong currentIdThread;
    /** daca este mesajul cu parinte sau mesajul camera curenta si stringul de prelucreat */
    private int index;
    /** mesajul curent care il va primi minerul */
    private Message messageCurrent;
    /** indica ca nici un wizard nu trimite un mesaj */
    private final long NO_ID = -1;

    private final int FIRST = 0;
    private final int SECOND = 1;

    /**
     * Creates a {@code CommunicationChannel} object.
     */
    public CommunicationChannel() {
        bufferMiner = new ArrayBlockingQueue<>(CAPACITY);
        bufferWizard = new ArrayBlockingQueue<>(CAPACITY);
        currentIdThread = new AtomicLong(NO_ID);
        index = FIRST;
        messageCurrent = new Message(-1, "ceva");
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

        /** permit dacat unui singur "magician" sa "comunice" */
        /** currentIdThread va reprezanta id-ul thread-ului care a reusit sa trimita primul mesajul */
        /** doar acel thread va putea sa comunice pana cand va trimite mesajul de end*/
        while (currentIdThread.get() != Thread.currentThread().getId()) {
            currentIdThread.compareAndSet(NO_ID, Thread.currentThread().getId());
        }

        /** "elibarare" thread-urilor in momentrul in care magicianul a terminat de comunicat */
        if (message.getData().equals(Wizard.END)) {
            currentIdThread.set(NO_ID);
            return;
        }

        /** mesajul de exit */
        if (message.getData().equals(Wizard.EXIT)) {
            try {
                bufferWizard.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return;
        }

        /** daca este mesajul in care se trimite parintele camerei curente */
        if (index == FIRST) {
            index = SECOND;
            messageCurrent.setParentRoom(message.getCurrentRoom());

            return;
        }

        /** daca este mesajul in care se trmite camera curenta si datele care trebuie sa fiu prelucrate */
        if (index == SECOND) {
            index = FIRST;
            messageCurrent.setData(message.getData());
            messageCurrent.setCurrentRoom(message.getCurrentRoom());

            try {
                /** compunerea uni mesaj complet pentru un miner */
                /** cele doua mesaje sunt compuse intr-un singur ca sa nu ocupam un spatiu dublu in buffer */
                bufferWizard.put(new Message(messageCurrent.getParentRoom(), messageCurrent.getCurrentRoom(),
                        messageCurrent.getData()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
