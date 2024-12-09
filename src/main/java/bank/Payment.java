package bank;

/**
 * Die Klasse Payment soll Ein- und Auszahlungen repräsentieren
 *
 * @author EGEHAN KILIC.
 */
public class Payment extends Transaction implements CalculateBill {

    private double incomingInterest;  // Einzahlung-Zinsen (0~1)
    private double outgoingInterest;  // Auszahlung-Zinsen (0~1)

    //Setter und Getter für alle Attribute
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

    public void setIncomingInterest(double incomingInterest) {
        if(incomingInterest >= 0.0 && incomingInterest <= 1.0) this.incomingInterest = incomingInterest;
        else System.out.println("Fehler beim Eingaben");
    }

    public double getIncomingInterest() {
        return incomingInterest;
    }

    public void setOutgoingInterest(double outgoingInterest) {
        if(outgoingInterest >= 0.0 && outgoingInterest <= 1.0) this.outgoingInterest = outgoingInterest;
        else System.out.println("Fehler beim Eingaben");
    }

    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Konstruktor mit 3 Parameter
     *
     * @param d date
     * @param a amount
     * @param des description
     */
    public Payment(String d, double a, String des) {
        super(d,a,des);
    }

    /**
     * Konstruktor mit 5 Parameter
     *
     * @param d date
     * @param a amount
     * @param des description
     * @param in incomingInterest
     * @param out outgoingInterest
     */
    public Payment(String d, double a, String des, double in, double out) {
        this(d,a,des);
        setIncomingInterest(in);
        setOutgoingInterest(out);
    }

    /**
     * Copy-Konstruktor
     *
     * @param p Payment-Objekt
     */
    public Payment(Payment p) {
        this(p.getDate(),p.getAmount(),p.getDescription(),p.getIncomingInterest(),p.getOutgoingInterest());
    }

    /**
     * calculate-Methode
     *
     * @return amount after incomming-or outcommingInterest
     */
    @Override
    public double calculate() {
        double result;
        if(getAmount() > 0.0)
            result = getAmount()*(1.0 - getIncomingInterest());
        else
            result = getAmount()*(1.0 + getOutgoingInterest());
        return result;
    }

    /**
     * overriding the toString() method for class Payment
     *
     * @return Date, Amount, Description, Incomminginterest, OutcommingInterest
     */
    @Override
    public String toString() {
        return (super.toString() +
                " IncommingInterest: " + this.incomingInterest+
                " OutcommingInterest: " + this.outgoingInterest);
    }

    /**
     * overriding the equals() method for class Payment
     *
     * @param obj an object of superclass Object
     * @return true if equals, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        Payment p = (Payment) obj;
        return (super.equals(p)) && (incomingInterest == p.incomingInterest) && (outgoingInterest == p.outgoingInterest);
    }
}
