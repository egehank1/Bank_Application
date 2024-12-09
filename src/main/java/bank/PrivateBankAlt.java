package bank;
import bank.exceptions.*;
import java.util.*;

/**
 * Klasse PrivateBank speichert Konten von Kunden
 *
 * @author EGEHAN KILIC
 */
public class PrivateBankAlt implements Bank {
    /**
     * Name der privaten Bank
     */
    private String name;
    /**
     * Zinsen bei der Einzahlung
     */
    private double incomingInterest;
    /**
     * Zinsen bei der Auszahlung
     */
    private double outgoingInterest;
    /**
     *  Konten mit Transaktionen
     */
    private final Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    // Setter und Getter
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }
    public double getIncomingInterest() {
        return this.incomingInterest;
    }
    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }

    public double getOutgoingInterest() {
        return this.outgoingInterest;
    }

    /**
     * Kontruktor
     *
     * @param name Name des Kontos
     * @param incomingInterest Zinsen bei der Einzahlung
     * @param outgoingInterest Zinsen bei der Auszahlung
     */
    public PrivateBankAlt(String name, double incomingInterest, double outgoingInterest) {
        setName(name);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * Copy-Konstruktur
     *
     * @param p PrivateBank-Objekt
     */
    public PrivateBankAlt(PrivateBank p) {
        this(p.getName(), p.getIncomingInterest(), p.getOutgoingInterest());
    }

    /**
     * overriding the toString() method for class PrivateBank
     *
     * @return Name+IncomingInterest+OutgoingInterest
     */
    @Override
    public String toString() {
        return ("Name: "+this.name+
                " IncomingInterest: "+this.incomingInterest+
                " OutgoingInterest: "+this.outgoingInterest);
    }

    /**
     * overriding the equals() method for class PrivateBank
     *
     * @return true if equals, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass())
            return false;
        PrivateBankAlt p = (PrivateBankAlt) obj;
        return (Objects.equals(name, p.name)) && (incomingInterest == p.incomingInterest) && (outgoingInterest == p.outgoingInterest);
    }

    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        // Überprüfe,ob das Konto bereits existiert
        if(accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: "+account);
        }
        // Wenn kein Problem existiert
        List<Transaction> new_list = new ArrayList<>();
        accountsToTransactions.put(account, new_list);
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account      the account to be added
     * @param transactions a list of already existing transactions which should be added to the newly created account
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, AccountDoesNotExistException {
        // Überprüfe,ob die Transaction bereits existiert
        /*for (List<Transaction> existingTransactions : accountsToTransactions.values()) {
            for (Transaction transaction : transactions) {
                if (existingTransactions.contains(transaction)) {
                    throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
                }
            }
        }*/

        // Wenn kein Problem existiert
        createAccount(account);
        for(Transaction transaction:transactions) addTransaction(account, transaction);
        //accountsToTransactions.put(account, transactions);
    }

    /**
     * Überprüfe die Attribute der Transaktion
     *
     * @param transaction Transaktion
     * @return true if gut, false if not
     */
    private boolean isTransaction_Valid(Transaction transaction) throws TransactionAttributeException {
        // Transaction ist Payment
        if(transaction instanceof Payment payment) {
            if(payment.getIncomingInterest() < 0.0 || payment.getIncomingInterest() > 1.0) {
                throw new TransactionAttributeException("Invalid Transaction: "+transaction);
            }
            if(payment.getOutgoingInterest() < 0.0 || payment.getOutgoingInterest() > 1.0) {
                throw new TransactionAttributeException("Invalid Transaction: "+transaction);
            }
        } else if(transaction instanceof Transfer transfer) {
            if(transfer.getAmount() < 0.0) {
                throw new TransactionAttributeException("Invalid Transaction: "+transaction);
            }
        }
        return true;
    }

    /**
     * Adds a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void addTransaction(String account, Transaction transaction)
            throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        // Überprüfe, ob die Transaction bereits existiert
        List<Transaction> transactions = accountsToTransactions.get(account);
        if (transactions!= null && transactions.contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
        }
        // Überprüfe, ob das Konto nicht existiert
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }
        // Überprüfe die Attribute der Transaktion
        if(isTransaction_Valid(transaction)) {
            // Wenn Transaction Payment ist, überschreibe die Zinsen-Wert
            if (transaction instanceof Payment payment) {
                payment.setIncomingInterest(this.incomingInterest); // Set from PrivateBank
                payment.setOutgoingInterest(this.outgoingInterest); // Set from PrivateBank
            }
        }
        // Wenn kein Problem existiert
        assert transactions != null;
        transactions.add(transaction);
    }

    /**
     * Removes a transaction from an account. If the transaction does not exist, an exception is
     * thrown.
     *
     * @param account     the account from which the transaction is removed
     * @param transaction the transaction which is removed from the specified account
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionDoesNotExistException if the transaction cannot be found
     */
    @Override
    public void removeTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionDoesNotExistException {
        // Überprüfe, ob das Konto nicht existiert
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }
        // Überprüfe, ob die Transaction nicht existiert
        if(!containsTransaction(account,transaction)) {
            throw new TransactionDoesNotExistException("transaction cannot be found " + transaction);
        }
        // Remove the transaction if it exists
        List<Transaction> transactions = accountsToTransactions.get(account);
        transactions.remove(transaction);
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        // Überprüfe, ob das Konto bereits existiert
        if (!accountsToTransactions.containsKey(account)) {
            return false;
        }
        // Checks whether the specified transaction for a given account exists
        List<Transaction> transactions = accountsToTransactions.get(account);
        return transactions.contains(transaction);
    }

    /**
     * Calculates and returns the current account balance.
     *
     * @param account the selected account
     * @return the current account balance
     */
    @Override
    public double getAccountBalance(String account) {
        // Nehme alle Transactionen von des Kontos
        List<Transaction> transactions = accountsToTransactions.get(account);
        double balance = 0.0;

        for(Transaction transaction : transactions) {
                if(transaction instanceof Transfer transfer) {
                    // Unterscheidung, ob Sender oder Empfänger mit dem Konto übereinstimmt
                    if(Objects.equals(transfer.getSender(), account)) {
                        // Wenn das Konto der Sender ist, handelt es sich um einen ausgehenden Transfer
                        balance -= transfer.calculate();
                    } else if (Objects.equals(transfer.getRecipient(), account)) {
                        // Wenn das Konto der Empfänger ist, handelt es sich um einen eingehenden Transfer
                        balance += transfer.calculate();
                    }
                } else if (transaction instanceof Payment payment) {
                    balance += payment.calculate();
                }
        }
        return balance;
    }

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        return accountsToTransactions.get(account);
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = getTransactions(account);

        // Sort transactions based on the calculated amount, in ascending or descending order
        if(asc) {
            transactions.sort(Comparator.comparing(Transaction::calculate));
        }
        else {
            transactions.sort(Comparator.comparing(Transaction::calculate).reversed());
        }
        return transactions;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> result = new ArrayList<>();
        for(Transaction transaction : transactions) {
            if(positive) {
                if(transaction.calculate() > 0.0) {
                    result.add(transaction);
                }
            }
            else {
                if(transaction.calculate() < 0.0) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }
}
