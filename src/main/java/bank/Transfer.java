package bank;

import java.util.Objects;

/**
 * Die Klasse Transfer soll im Kontext von Überweisungen verwendet werden
 *
 * @author EGEHAN KILIC.
 */
public class Transfer extends Transaction implements CalculateBill {

    protected String sender;      // Sender-Name
    protected String recipient;   // Empfänger-Name

    // Setter und Getter für allte Attribute
    public void setDate(String date) {
        super.setDate(date);
    }

    public String getDate() {
        return super.getDate();
    }


    public void setAmount(double amount) {
        super.setAmount(amount);
    }

    public double getAmount() {
        return super.getAmount();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public Transfer(){}

    /**
     * Konstruktor mit 3 Parameter
     *
     * @param d date
     * @param a amount
     * @param des description
     */
    public Transfer(String d, double a, String des) {
        super(d,a,des);
    }

    /**
     * Konstruktor mit 5 Parameter
     *
     * @param d date
     * @param a amount
     * @param des description
     * @param s Sender
     * @param r Recipient
     */
    public Transfer(String d, double a, String des, String s, String r) {
        super(d,a,des);
        setSender(s);
        setRecipient(r);
    }

    /**
     * Copy-Konstruktor
     *
     * @param t Transfer-Objekt
     */
    // durch incoming und outgoing transfer wird getested.
    public Transfer(Transfer t) {
        this(t.getDate(),t.getAmount(),t.getDescription(),t.getSender(),t.getRecipient());
    }

    /**
     * calculate-Methode
     *
     * @return amount
     */
    //durch balance wird das schon getestet testaccountbalacne privatebanktest
    @Override
    public double calculate() {
        return getAmount();
    }

    /**
     * overriding the toString() method for class Transfer
     *
     * @return  Date, Amount, Description, Sender, Recipient
     */
    @Override
    public String toString() {
        return (super.toString()+
                " Sender: " + this.sender+
                " Recipient: " + this.recipient);
    }

    /**
     * overriding the equals() method for class Transfer
     *
     * @param obj an object of superclass Object
     * @return true if equals, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // If both are the same instance, return true.
        }
        if (obj == null) {
            return false; // If the other object is null, return false.
        }
        if (getClass() != obj.getClass()) {
            return false; // If the classes are different, return false.
        }
        Transfer t = (Transfer) obj; // Safe to cast now since we checked the class.
        return super.equals(t) && Objects.equals(sender, t.sender) && Objects.equals(recipient, t.recipient);
    }

}
