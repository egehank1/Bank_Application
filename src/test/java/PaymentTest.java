import bank.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    Payment payment1;
    Payment payment2;

    // tells JUnit to run the init() method before each individual test method in the test class.
    @BeforeEach
    void init() {
        payment1 = new Payment("2024-11-17", 400.0, "payment1", 0.02, 0.03);
        payment2 = new Payment("2024-11-18", -300.0, "payment2", 0.01, 0.02);
    }

    // 2 Eigene Attribute, 3 Inheritated Attribute, sollen insgesamt 5 Testcases.
    @Test
    void testSetterr() {
        payment1.setDate("24.11.2024");
        assertEquals(payment1.getDate(), "24.11.2024");

        payment1.setAmount(-400.0);
        assertEquals(payment1.getAmount(), -400.0);

        payment1.setDescription("newdes");
        assertEquals(payment1.getDescription(), "newdes");

        payment1.setIncomingInterest(1.05);
        assertNotEquals(payment1.getIncomingInterest(), 1.05);

        payment1.setOutgoingInterest(1.05);
        assertNotEquals(payment1.getOutgoingInterest(), 1.05);
    }
    // 2 Constructors einmal mit 3 und einmal mit 5 attribute,
    // Test für eine reicht, da constructor mit 5 attribute nutzt CONSTRUCTOR CHAINING.
    @Test
    void testConstructor() {
        assertEquals(400.0, payment1.getAmount());
        assertEquals("payment1", payment1.getDescription());
        assertEquals(0.02, payment1.getIncomingInterest());
        assertEquals(0.03, payment1.getOutgoingInterest());

        assertEquals(-300.0, payment2.getAmount());
        assertEquals("payment2", payment2.getDescription());
        assertEquals(0.01, payment2.getIncomingInterest());
        assertEquals(0.02, payment2.getOutgoingInterest());
    }



    @Test
    void testCopyConstructor() {
        Payment copyPayment1 = new Payment(payment1);
        assertEquals(payment1, copyPayment1);

        Payment copyPayment2 = new Payment(payment2);
        assertEquals(payment2, copyPayment2);
    }

    @Test
    void testCalculate() {
        assertEquals(392.0, payment1.calculate()); // Amount after incoming interest
        assertEquals(-306.0, payment2.calculate()); // Amount after outgoing interest
    }

    @Test
    void testEquals() {
        Payment copyPayment1 = new Payment(payment1);
        assertTrue(payment1.equals(copyPayment1));
    }

    @Test
    void testToString() {
        String str_payment1 = "Date: 2024-11-17 Amount: 392.0 Description: payment1 IncommingInterest: 0.02 OutcommingInterest: 0.03";
        assertEquals(str_payment1, payment1.toString());

        String str_payment2 = "Date: 2024-11-18 Amount: -306.0 Description: payment2 IncommingInterest: 0.01 OutcommingInterest: 0.02";
        assertEquals(str_payment2, payment2.toString());
    }


}