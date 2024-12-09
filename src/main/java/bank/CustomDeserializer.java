package bank;

import com.google.gson.*;
import java.lang.reflect.Type;

public class CustomDeserializer implements JsonDeserializer<Transaction> {

    /**
     * The deserialize method handles the conversion of a JSON representation into a Transaction object.
     *
     * @param json      The JSON data as a JsonElement
     * @param typeOfT   The type of the object to deserialize (not used directly here)
     * @param context   The deserialization context, allowing for recursive deserialization
     * @return          A deserialized Transaction object of the appropriate subclass
     * @throws JsonParseException If the JSON data cannot be deserialized or contains an unknown class
     */
    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // JsonElement into a JsonObject not a string num usw.
        JsonObject jsonObject = json.getAsJsonObject();

        String className = jsonObject.get("CLASSNAME").getAsString();

        // instanzJSON objekt mit werten erstellen
        JsonObject instanceObject = jsonObject.get("INSTANCE").getAsJsonObject();

        // Use a switch statement to match the class name and deserialize into the appropriate subclass
        switch (className) {
            case "Payment":
                // Deserialize into a Payment object using the context
                return context.deserialize(instanceObject, Payment.class);
            case "IncomingTransfer":
                // Deserialize into an IncomingTransfer object using the context
                return context.deserialize(instanceObject, IncomingTransfer.class);
            case "OutgoingTransfer":
                // Deserialize into an OutgoingTransfer object using the context
                return context.deserialize(instanceObject, OutgoingTransfer.class);
            default:
                // Throw an exception if the class name is not recognized
                throw new JsonParseException("Unknown class: " + className);
        }
    }
}
