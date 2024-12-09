import bank.IncomingTransfer;
import bank.OutgoingTransfer;
import bank.Payment;
import bank.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    IncomingTransfer incomingTransfer;
    OutgoingTransfer outgoingTransfer;

    @BeforeEach
    void init() {
        incomingTransfer = new IncomingTransfer("2024-11-17", 400.0, "incomingTransfer", "Elena", "Vinicius");
        outgoingTransfer = new OutgoingTransfer("2024-11-18", 300.0, "outgoingTransfer", "Vinicius", "Elena");
    }

    @Test
    void testSetterr() {
        incomingTransfer.setDate("24.11.2024");
        assertEquals(incomingTransfer.getDate(), "24.11.2024");
        incomingTransfer.setAmount(-400.0);
        assertEquals(incomingTransfer.getAmount(), -400.0);
        incomingTransfer.setDescription("newdes");
        assertEquals(incomingTransfer.getDescription(), "newdes");
        incomingTransfer.setSender("newSender");
        assertEquals(incomingTransfer.getSender(), "newSender");
        incomingTransfer.setRecipient("newRecipient");
        assertEquals(incomingTransfer.getRecipient(), "newRecipient");
    }

    @Test
    void testConstructor() {
        assertEquals(400.0, incomingTransfer.getAmount());
        assertEquals("incomingTransfer", incomingTransfer.getDescription());
        assertEquals("Elena", incomingTransfer.getSender());
        assertEquals("Vinicius", incomingTransfer.getRecipient());

        assertEquals(300.0, outgoingTransfer.getAmount());
        assertEquals("outgoingTransfer", outgoingTransfer.getDescription());
        assertEquals("Vinicius", outgoingTransfer.getSender());
        assertEquals("Elena", outgoingTransfer.getRecipient());
    }

    @Test
    void testCopyConstructor() {
        IncomingTransfer copyincomingTransfer = new IncomingTransfer(incomingTransfer);
        assertEquals(incomingTransfer, copyincomingTransfer);

        OutgoingTransfer copyoutgoingTransfer = new OutgoingTransfer(outgoingTransfer);
        assertEquals(outgoingTransfer, copyoutgoingTransfer);
    }

    @Test
    void testCalculate() {
        assertEquals(400.0, incomingTransfer.calculate()); // Amount after incoming interest
        assertEquals(-300.0, outgoingTransfer.calculate()); // Amount after outgoing interest
    }

    @Test
    void testEquals() {
        IncomingTransfer copyincomingTransfer = new IncomingTransfer(incomingTransfer);
        assertTrue(incomingTransfer.equals(copyincomingTransfer));

        OutgoingTransfer copyoutgoingTransfer = new OutgoingTransfer(outgoingTransfer);
        assertTrue(outgoingTransfer.equals(copyoutgoingTransfer));
    }

    @Test
    void testToString() {
        String str_incomingTransfer = "Date: 2024-11-17 Amount: 400.0 Description: incomingTransfer Sender: Elena Recipient: Vinicius";
        assertEquals(str_incomingTransfer, incomingTransfer.toString());

        String str_outgoingTransfer = "Date: 2024-11-18 Amount: -300.0 Description: outgoingTransfer Sender: Vinicius Recipient: Elena";
        assertEquals(str_outgoingTransfer, outgoingTransfer.toString());
    }

}
