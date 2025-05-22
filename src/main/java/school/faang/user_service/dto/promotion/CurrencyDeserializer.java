package school.faang.user_service.dto.promotion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Currency;

public class CurrencyDeserializer extends JsonDeserializer<Currency> {

    @Override
    public Currency deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String currencyCode = p.getText().toUpperCase();
        try {
            return Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Invalid currency code: %s", currencyCode));
        }
    }
}
