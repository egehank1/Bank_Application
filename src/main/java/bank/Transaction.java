package bank;

import java.util.Objects;

/**
 * Abstrakte Klasse Transaction
 *
 *
 * @author EGEHAN KILIC
 */
public abstract class Transaction implements CalculateBill {
    /**
     * Zeitpunkt in form(DD.MM.YYYY)
     */
    protected String date;
    /**
     * Geldmenge (negativ bei Auszahlung - positiv bei Einzahlung)
     */
    protected double amount;
    /**
     * Beschreibung des Vorgangs
     */
    protected String description;

    /**
     * Konstruktor
     *
     * @param date Zeitpunkt
     * @param amount Geldmenge
     * @param description Beschreibung
     */
    public Transaction(String date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    Transaction() {}

    //Getter und Setter
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * overriding the toString() method
     *
     * @return Date, Amount, Description
     */
    @Override
    public String toString() {
        return ("Date: "+this.date+
                " Amount: "+calculate()+
                " Description: "+this.description);
    }

    /**
     * overriding the equals() method
     *
     * @return true if equals, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        Transaction t = (Transaction) obj;
        return (Objects.equals(date, t.date)) && (amount == t.amount) && (Objects.equals(description, t.description));
    }
}
