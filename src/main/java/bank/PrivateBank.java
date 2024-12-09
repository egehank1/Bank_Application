package bank;
import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Klasse PrivateBank speichert Konten von Kunden
 *
 * @author EGEHAN KILIC
 */
public class PrivateBank implements Bank {
    /**
     * Name der privaten Bank
     */
    private String name;
    /**
     * Name des Ordners zum Konto-Speichern
     */
    private String directoryName;
    /**
     * Zinsen bei der Einzahlung
     */
    private double incomingInterest;
    /**
     * Zinsen bei der Auszahlung
     */
    private double outgoingInterest;
    private Path fullPath;
    /**
     *  Konten mit Transaktionen
     */
    private final Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    /**
     * Setzt den Namen.
     *
     * @param name Der neue Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt den Namen zurück.
     *
     * @return Der Name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setzt den Speicherort der Bank.
     *
     * @param directoryName Der neue Speicherort.
     */
    public void setDirectoryName(String directoryName) {
        if (directoryName == null || directoryName.isEmpty()) {
            throw new IllegalArgumentException("Directory name cannot be null or empty.");
        }
        this.directoryName = directoryName;
    }

    /**
     * Setzt den Zinssatz für eingehende Zahlungen.
     *
     * @param incomingInterest Der Zinssatz für eingehende Zahlungen.
     */
    public void setIncomingInterest(double incomingInterest) {
        this.incomingInterest = incomingInterest;
    }

    /**
     * Gibt den Speicherort der Bank zurück.
     *
     * @return Der Speicherort als String.
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Gibt den Zinssatz für eingehende Zahlungen zurück.
     *
     * @return Der Zinssatz für eingehende Zahlungen.
     */
    public double getIncomingInterest() {
        return this.incomingInterest;
    }

    /**
     * Setzt den Zinssatz für ausgehende Zahlungen.
     *
     * @param outgoingInterest Der Zinssatz für ausgehende Zahlungen.
     */
    public void setOutgoingInterest(double outgoingInterest) {
        this.outgoingInterest = outgoingInterest;
    }
    public Path getFullPath() {
        return fullPath;
    }

    /**
     * Gibt den Zinssatz für ausgehende Zahlungen zurück.
     *
     * @return Der Zinssatz für ausgehende Zahlungen.
     */
    public double getOutgoingInterest() {
        return this.outgoingInterest;
    }


    /**
     * Kontruktor
     *
     * @param name Name des Kontos
     * @param directoryName Der Speicherort für Konten und Transaktionen.
     * @param incomingInterest Zinsen bei der Einzahlung
     * @param outgoingInterest Zinsen bei der Auszahlung
     */
    public PrivateBank(String name,String directoryName, double incomingInterest, double outgoingInterest) {

        setName(name);
        setDirectoryName(directoryName);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);

        //Erstelle neues Verzeichnis,wenn nicht existiert.
        File directory = new File(directoryName);
        if(!directory.exists()) {
            directory.mkdir();
        } else {
            try {
                readAccounts();
            } catch (IOException e) {
                System.out.println("Error while reading account" + e.getMessage());
            }
        }
    }

    /**
     * Copy-Konstruktur
     *
     * @param p PrivateBank-Objekt
     */
    public PrivateBank(PrivateBank p) {
        this(p.getName(),p.getDirectoryName(), p.getIncomingInterest(), p.getOutgoingInterest());
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
        PrivateBank p = (PrivateBank) obj;
        return (Objects.equals(name, p.name)) && Objects.equals(directoryName, p.directoryName) && (incomingInterest == p.incomingInterest) && (outgoingInterest == p.outgoingInterest);
    }

    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        // Überprüfe,ob das Konto bereits existiert
        if(accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: "+account);
        }
        // Wenn kein Problem existiert
        List<Transaction> new_list = new ArrayList<>();
        accountsToTransactions.put(account, new_list);
        writeAccount(account);
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
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, AccountDoesNotExistException, IOException {

        createAccount(account);
        for(Transaction transaction:transactions) addTransaction(account, transaction);
    }

    /**
     * Überprüfe die Attribute der Transaktion
     *
     * @param transaction Transaktion
     * @return true if gut, false if not
     */
    public boolean isTransaction_Valid(Transaction transaction) throws TransactionAttributeException {
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
            throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        // Überprüfe die Attribute der Transaktion
        if(isTransaction_Valid(transaction)) {
            // Wenn Transaction Payment ist, überschreibe die Zinsen-Wert
            if (transaction instanceof Payment payment) {
                payment.setIncomingInterest(this.incomingInterest); // Set from PrivateBank
                payment.setOutgoingInterest(this.outgoingInterest); // Set from PrivateBank
            }
        }
        // Überprüfe, ob die Transaction bereits existiert
        if (containsTransaction(account,transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists: " + transaction);
        }
        // Überprüfe, ob das Konto nicht existiert
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }
        // Wenn kein Problem existiert
        List<Transaction> transactions = accountsToTransactions.get(account);
            assert transactions != null;
            transactions.add(transaction);
            writeAccount(account);
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
            throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
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
        writeAccount(account);
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
            balance += transaction.calculate();
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

    /**
     * Alle vorhandenen Konten vom Dateisystem lesen.
     *
     * @throws IOException Wenn ein Fehler beim Lesen der Datei auftritt.
     */
    public void readAccounts() throws IOException {
        // Gson-Instanz erstellen
        Gson gson = new GsonBuilder() // customize how gson behaves
                .registerTypeAdapter(Transaction.class, new CustomDeserializer()) // nutze die logik vom customserializer wenn ein transaction object occurs
                .setPrettyPrinting() // nicht alles in einf eine linie
                .create(); // generiere gson instanz

        File directory = new File(getDirectoryName());

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("The specified directory does not exist or is not a directory: " + directory);
        }

        File[] files = directory.listFiles();
        if ((files != null) || (files.length != 0)) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    try {
                        // Dateiinhalt als String lesen
                        String json_daten = new String(Files.readAllBytes(file.toPath())); // NIO (New I/O) API

                        // Deserialisieren des JSON-Strings in eine Transaktionsliste
                        // GSONFROMJSON
                        List<Transaction> transactions = gson.fromJson(json_daten, new TypeToken<List<Transaction>>() {}.getType());

                        // Konto erstellen und Transaktionen hinzufügen
                        String accountName = file.getName().replace(".json", "").replace("Konto ", "");
                        createAccount(accountName, transactions);

                        System.out.println("Successfully read account: " + accountName);

                    } catch (AccountAlreadyExistsException |
                             TransactionAlreadyExistException | TransactionAttributeException |
                             AccountDoesNotExistException e) {
                        throw new RuntimeException("Error while creating account or adding transactions: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Angegebene Konto im Dateisystem persistieren.
     *
     * @throws IOException Wenn ein Fehler beim Lesen der Datei auftritt.
     */
    public void writeAccount(String account) throws IOException {
        // Gson-Instanz erstellen
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Transaction.class, new CustomSerializer()) // nutze die logik vom customserializer wenn ein transaction object occurs
                .setPrettyPrinting()
                .create();

        // Serialisieren des Transaction-Array in ein JSON-Strings
        List<Transaction> transactions = getTransactions(account);

        // JSON-Element aus der Liste erstellen
        //  List<Transaction>  zum json
        JsonElement element = gson.toJsonTree(transactions, new TypeToken<List<Transaction>>() {}.getType());

        /*
        String[] jsonObjectArray = new String[100];
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);

            // Transaction in ein JsonElement umwandeln
            JsonElement jsonElement = gson.toJsonTree(transaction);

            // JsonElement in ein JsonObject konvertieren
            if (jsonElement.isJsonObject()) {
                String jsonObjectElement = String.valueOf(jsonElement.getAsJsonObject());
                jsonObjectArray[i] = jsonObjectElement;
            }
        }

        String jsonArray = Arrays.toString(jsonObjectArray);
        */


        // Verzeichnis überprüfen oder erstellen
        File directory = new File(getDirectoryName());
        if (!directory.exists() && !directory.mkdirs()) { // if dir exists if path exists
            throw new IOException("Failed to create directory: " + directory);
        }

        // Datei für das angegebene Konto erstellen oder finden
        File accountFile = new File(directory, "Konto " + account + ".json");
        if (!accountFile.exists()) {
            boolean created = accountFile.createNewFile();
            if (!created) {
                throw new IOException("Failed to create the account file: " + accountFile.getName());
            }
        }
        // improves write performance by reducing the number of I/O operations.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountFile))) {
        // SERIALISIEREN== GSONTOJSON
            gson.toJson(element, writer);
        } catch (IOException e) {
            throw new IOException("Error writing account data to file: " + accountFile.getPath(), e);
        }
    }

    public List<String> getAllAccounts() {
        Set<String> set = accountsToTransactions.keySet();
        return new ArrayList<>(set);
    }
    // wird in MainController benutzt um Kontos zu loeschen.
    public void deleteAccount(String account) throws  AccountDoesNotExistException, IOException{
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException("Account " + account + " does not exist.");}
        else{
            accountsToTransactions.remove(account);
            Path path= Path.of(this.getFullPath()+"/"+account+".json");
            Files.deleteIfExists(path);
        }
    }
}











// JSON-Daten in die Datei schreiben -- Problem: Alle Transaktion von Konto überschreiben
        /*try (FileWriter writer = new FileWriter(accountFile)) {
            writer.write(String.valueOf(element)+ "\n");
        } catch (IOException e) {
            throw new IOException("Error writing account data to file: " + accountFile.getPath(), e);
        }*/