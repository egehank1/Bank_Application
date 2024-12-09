import bank.Payment;
import bank.PrivateBankAlt;
import bank.Transaction;
import bank.Transfer;
import bank.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class PrivateBankAltTest {

    PrivateBankAlt bank;

    @BeforeEach
    void setUp() {
        bank = new PrivateBankAlt("TestBank", 0.02, 0.01);
    }

    @Test
    void testCreateAccount() throws AccountAlreadyExistsException {
        bank.createAccount("testAccount");
        assertTrue(bank.getTransactions("testAccount").isEmpty());
    }

    @Test
    void testCreateAccountAlreadyExists() {
        assertThrows(AccountAlreadyExistsException.class, () -> {
            bank.createAccount("testAccount");
            bank.createAccount("testAccount"); // This should throw an exception
        });
    }

    @Test
    void testAddTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, TransactionAttributeException {
        bank.createAccount("testAccount");
        // Assuming Transaction is a valid class with a constructor
        Transaction transaction = new Payment("2024-11-20", 200.0, "Test Payment", 0.3, 0.2);
        bank.addTransaction("testAccount", transaction);

        // Verify transaction is added
        assertTrue(bank.containsTransaction("testAccount", transaction));
    }

    @Test
    void testAddTransactionAccountDoesNotExist() {
        Transaction transaction = new Payment("2024-11-20", 200.0, "Test Payment", 1.0, 0.2);
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.addTransaction("nonExistingAccount", transaction);
        });
    }

    @Test
    void testRemoveTransaction() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, TransactionDoesNotExistException {
        bank.createAccount("testAccount");
        Transaction transaction = new Payment("2024-11-20", 200.0, "Test Payment", 1.0, 0.3);
        bank.addTransaction("testAccount", transaction);

        // Remove transaction
        bank.removeTransaction("testAccount", transaction);
        assertFalse(bank.containsTransaction("testAccount", transaction));
    }

    @Test
    void testGetAccountBalance() throws AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        bank.createAccount("testAccount");
        Transaction deposit = new Payment("2024-11-20", 500.0, "Deposit", 1.0, 1.0);
        bank.addTransaction("testAccount", deposit);

        assertEquals(490.0, bank.getAccountBalance("testAccount"));
    }
    @Test
    void testDefaultConstructor() {
        Transfer transfer = new Transfer();

        // Assuming the default constructor initializes the date, amount, and description to some default values (null or zero).
        assertNull(transfer.getDate());
        assertEquals(0.0, transfer.getAmount());
        assertNull(transfer.getDescription());
    }

    @Test
    void testConstructorWithThreeParameters() {
        String date = "2024-11-20";
        double amount = 1000.0;
        String description = "Test Transfer";

        Transfer transfer = new Transfer(date, amount, description);

        assertEquals(date, transfer.getDate());
        assertEquals(amount, transfer.getAmount());
        assertEquals(description, transfer.getDescription());
    }

    @Test
    void testConstructorWithFiveParameters() {
        String date = "2024-11-20";
        double amount = 1000.0;
        String description = "Test Transfer";
        String sender = "SenderAccount";
        String recipient = "RecipientAccount";

        Transfer transfer = new Transfer(date, amount, description, sender, recipient);

        assertEquals(date, transfer.getDate());
        assertEquals(amount, transfer.getAmount());
        assertEquals(description, transfer.getDescription());
        assertEquals(sender, transfer.getSender());
        assertEquals(recipient, transfer.getRecipient());
    }

}