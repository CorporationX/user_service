package school.faang.user_service.dto;

import school.faang.user_service.exception.IllegalEntityException;

import java.util.Arrays;

public enum Currency {
    USD,
    EUR;

    public static Currency getFromName(String currencyName) {
        return Arrays.stream(Currency.values())
            .filter(currency -> currency.name().equals(currencyName.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new IllegalEntityException(String.format("Currency with name: %s not found", currencyName)));
    }
}