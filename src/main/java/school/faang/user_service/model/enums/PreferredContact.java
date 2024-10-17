package school.faang.user_service.model.enums;

public enum PreferredContact {
    EMAIL,
    SMS,
    TELEGRAM;

    public static PreferredContact fromString(String preference) {
        for (PreferredContact contact : PreferredContact.values()) {
            if (contact.name().equalsIgnoreCase(preference)) {
                return contact;
            }
        }
        throw new IllegalArgumentException("No contact preference with name " + preference + " found");
    }
}