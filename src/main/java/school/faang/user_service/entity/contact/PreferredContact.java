package school.faang.user_service.entity.contact;

import school.faang.user_service.exception.invalidFieldException.InvalidEnumValueException;

public enum PreferredContact {
    EMAIL, PHONE, TELEGRAM;

    public static PreferredContact fromString(String preference) {
        for (PreferredContact contact : PreferredContact.values()) {
            if (contact.name().equalsIgnoreCase(preference)) {
                return contact;
            }
        }
        throw new InvalidEnumValueException("No contact preference with name " + preference + " found");
    }
}
