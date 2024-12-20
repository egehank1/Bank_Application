package bank;

public class IncomingTransfer extends Transfer implements CalculateBill {

    /**
     * Konstruktor
     *
     * @param d Date
     * @param a Amount
     * @param des Description
     * @param s Sender
     * @param r Recipient
     */
    public IncomingTransfer(String d, double a, String des, String s, String r) {
        super(d,a,des,s,r);
    }

    /**
     * Copy-Konstruktor
     *
     * @param t Transfer-Objekt
     */
    public IncomingTransfer(IncomingTransfer t) {
        this(t.getDate(),t.getAmount(),t.getDescription(),t.getSender(),t.getRecipient());
    }

    /**
     * calculate-Methode
     *
     * @return amount
     */
    @Override
    public double calculate() {
        return getAmount();
    }
}
