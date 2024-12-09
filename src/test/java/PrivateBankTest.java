import bank.*;
import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PrivateBankTest {

    PrivateBank bank;
    Payment payment0;
    Payment payment1;
    IncomingTransfer incomingTransfer0;
    OutgoingTransfer outgoingTransfer0;
    // 196 - 206 + 100 - 400
    // Payment nimmt wieder in out interest vom privatebank.
    @BeforeEach
    void init() {
        bank = new PrivateBank("TestBank", "testDirectory", 0.02, 0.03);
        payment0 = new Payment("22.11.2024", 200.0, "Payment0");
        payment1 = new Payment("20.11.2024", -200.0, "Payment1", 0.01, 0.02);
        incomingTransfer0 = new IncomingTransfer("22.11.2024", 100.0, "IncomingTransfer","SenderI", "RecipientI");
        outgoingTransfer0 = new OutgoingTransfer("20.11.2024", 400.0, "OutgoingTransfer","SenderO", "RecipientO");
    }

    @AfterEach
    void cleanup() {
        // Speicherpurposes
        File directory = new File("testDirectory");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.err.println("Failed to delete file: " + file.getName());
                    }
                }
            }
            if (!directory.delete()) {
                System.err.println("Failed to delete directory: " + directory.getName());
            }
        }
    }

    @Test
    void testConstructor() {
        assertEquals("TestBank", bank.getName());
        assertEquals(0.02, bank.getIncomingInterest());
        assertEquals(0.03, bank.getOutgoingInterest());
    }

    @Test
    void testCopyConstructor() {
        PrivateBank Copybank = new PrivateBank(bank);
        assertEquals("TestBank", Copybank.getName());
        assertEquals(0.02, Copybank.getIncomingInterest());
        assertEquals(0.03, Copybank.getOutgoingInterest());
    }

    @Test
    void testCreateAccount() throws Exception {
        assertDoesNotThrow(() -> bank.createAccount("Account"));
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("Account"));
    }

    @ParameterizedTest // Multiple times exekutieren mit andere inputs.
    @ValueSource(doubles = {100.0, -200.0, 50.0, -500.0})
    // (parameters) -> { body } lambda expression Defines an inline anonymous function.
    // außen eine schleife die schon alle durchlaufen,
    void testAddTransaction(double amount) throws Exception {
        bank.createAccount("TestAccount");
        IncomingTransfer incomingTransfer = new IncomingTransfer("20.11.2024",amount,"Test","Sender","Recipient");
        if(amount > 0.0) {
            // Positive Transactionen sind akzeptiert.
            assertDoesNotThrow(() -> bank.addTransaction("TestAccount", incomingTransfer));
        } else {
            // Negative werden dagegen nicht, sowie -200.0 oder -500.0.
            assertThrows(TransactionAttributeException.class, () -> bank.addTransaction("TestAccount", incomingTransfer));
        }
        // Nachdem die vAliditat der menge überprüft ist, überprüft man die hinzufügenprozess.(zb ob der transaction BEREITS EXISTIERT.) throws == signature of the methos
        // addieren beim ersten mal, soll keinen Fehler verursachen!
        assertDoesNotThrow(() -> bank.addTransaction("TestAccount", payment0));
        // Nochmal addieren, wir erwarten einen transalreadyexists exception.
        // the exception ist thrown, test passes
        assertThrows(TransactionAlreadyExistException.class, () -> bank.addTransaction("TestAccount", payment0));
        // Verifies that adding a transaction to a non-existent account (e.g., "NotExistAccount") throws an AccountDoesNotExistException.
        assertThrows(AccountDoesNotExistException.class, () -> bank.addTransaction("NotExistAccount", payment0));
    }

    @Test
    void testremoveTransaction() throws Exception {
        bank.createAccount("TestAccount");
        // keine transactionen hünzigefügt, also exception erwartet
        assertThrows(TransactionDoesNotExistException.class, () -> bank.removeTransaction("TestAccount",payment0));
        bank.addTransaction("TestAccount",payment0);
        // accountname soll gleich sein
        assertThrows(AccountDoesNotExistException.class, () -> bank.removeTransaction("NotExistAccount",payment0));
        // sonst erwartet kein exception
        assertDoesNotThrow(() -> bank.removeTransaction("TestAccount",payment0));
    }

    @Test
    void testcontainsTransaction() throws IOException, AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        bank.createAccount("TestAccount"); // sonrasında sil bunu
        bank.addTransaction("TestAccount", payment0);
        assertTrue(bank.containsTransaction("TestAccount", payment0));
        assertFalse(bank.containsTransaction("TestAccount", payment1));
    }

    @Test
    void testAccountBalance() throws Exception {
        bank.createAccount("TestAccount");
        bank.addTransaction("TestAccount", payment0);
        bank.addTransaction("TestAccount", payment1);
        bank.addTransaction("TestAccount", incomingTransfer0);
        bank.addTransaction("TestAccount", outgoingTransfer0);

        assertEquals(-310.0, bank.getAccountBalance("TestAccount")); // 196 - 206 + 100 - 400
    }

    @Test
    void testgetTransactions() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("TestAccount");
        bank.addTransaction("TestAccount",payment0);
        bank.addTransaction("TestAccount",incomingTransfer0);
        // eine temp liste erstellen
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(payment0);
        transactions.add(incomingTransfer0);
        // beide listen vergleichen
        assertEquals(transactions, bank.getTransactions("TestAccount"));
    }

    @Test
    void testgetTransactionsSorted() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("TestAccount");
        Transaction payment = new Payment("20.11.2024", 100.0, "test", 0.02, 0.03);
        Transaction incomingTransfer = new IncomingTransfer("21.11.2024", 200.0, "Test", "Sender", "Recipient");
        bank.addTransaction("TestAccount",payment);
        bank.addTransaction("TestAccount",incomingTransfer);
        //Sorted-Liste
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(payment);
        transactions.add(incomingTransfer);
        //Reversed Sorted-Liste
        List<Transaction> revtransactions = new ArrayList<>();
        revtransactions.add(incomingTransfer);
        revtransactions.add(payment);
        // ascending= 0 1 4 11 33
        assertEquals(transactions, bank.getTransactionsSorted("TestAccount",true));
        assertEquals(revtransactions, bank.getTransactionsSorted("TestAccount",false));
    }
    // GUCKT OB EINGABE ODER AUSGABE durch bool positive
    @Test
    void testgetTransactionsByType() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {
        bank.createAccount("TestAccount");
        Transaction payment = new Payment("20.11.2024", -100.0, "test", 0.02, 0.03);
        Transaction incomingTransfer = new IncomingTransfer("21.11.2024", 200.0, "Test", "Sender", "Recipient");
        bank.addTransaction("TestAccount",payment);
        bank.addTransaction("TestAccount",incomingTransfer);

        //Positive Liste
        List<Transaction> pos_transactions = new ArrayList<>();
        pos_transactions.add(incomingTransfer);
        //Negative Liste
        List<Transaction> nev_transactions = new ArrayList<>();
        nev_transactions.add(payment);

        assertEquals(pos_transactions, bank.getTransactionsByType("TestAccount",true));
        assertEquals(nev_transactions, bank.getTransactionsByType("TestAccount",false));
    }
    // HIER IST CLEANUP WICHTIG.
    @Test
    void testWriteReadAccount() throws IOException, AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        bank.createAccount("TestAccount");

        // Beispieltransaktionen hinzufügen
        bank.addTransaction("TestAccount", payment0);
        bank.addTransaction("TestAccount", payment1);
        bank.addTransaction("TestAccount", incomingTransfer0);
        bank.addTransaction("TestAccount", outgoingTransfer0);

        // Die Testdaten werden im Verzeichnis persistiert
        bank.writeAccount("TestAccount");
        // The bank reads the persisted data from the directory "testDirectory" where "TestAccount" was stored. GANZ OBEN.
        PrivateBank newbank = new PrivateBank ("NewTestBank", "testDirectory", 0.02, 0.03);
        assertTrue(newbank.containsTransaction("TestAccount",payment0));
        assertTrue(newbank.containsTransaction("TestAccount",payment1));
        assertTrue(newbank.containsTransaction("TestAccount",incomingTransfer0));
        assertTrue(newbank.containsTransaction("TestAccount",outgoingTransfer0));

    }

    @Test
    void testEquals() {
        PrivateBank sameBank = new PrivateBank(bank);
        assertEquals(bank, sameBank);
    }

    @Test
    void testToString() {
        String expected = "Name: TestBank IncomingInterest: 0.02 OutgoingInterest: 0.03";
        assertEquals(expected, bank.toString());
    }
}
