package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

public class CustomSerializer implements JsonSerializer<Transaction> {
    @Override
    public JsonElement serialize(Transaction transaction, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // CLASSNAME, getsimplename = classname ohne packagename wie bank.transfer usw
        jsonObject.addProperty("CLASSNAME", transaction.getClass().getSimpleName());

        // INSTANCE: Direkte Serialisierung des Objekts
        JsonObject instanceObject = new JsonObject();
        if (transaction instanceof Payment) {
            Payment payment = (Payment) transaction; // cast erstmal
            instanceObject.addProperty("incomingInterest", payment.getIncomingInterest());
            instanceObject.addProperty("outgoingInterest", payment.getOutgoingInterest());
        } else if (transaction instanceof IncomingTransfer) {
            IncomingTransfer incomingTransfer = (IncomingTransfer) transaction;
            instanceObject.addProperty("sender", incomingTransfer.getSender());
            instanceObject.addProperty("recipient", incomingTransfer.getRecipient());
        } else if (transaction instanceof OutgoingTransfer) {
            OutgoingTransfer outgoingTransfer = (OutgoingTransfer) transaction;
            instanceObject.addProperty("sender", outgoingTransfer.getSender());
            instanceObject.addProperty("recipient", outgoingTransfer.getRecipient());
        }

        // Gemeinsame Felder
        instanceObject.addProperty("date", transaction.getDate());
        instanceObject.addProperty("amount", transaction.getAmount());
        instanceObject.addProperty("description", transaction.getDescription());

        jsonObject.add("INSTANCE", instanceObject);

        return jsonObject;
    }
}
