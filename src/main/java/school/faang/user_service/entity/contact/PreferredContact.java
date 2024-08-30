package school.faang.user_service.entity.contact;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PreferredContact {
    EMAIL, PHONE, TELEGRAM;

    public static PreferredContact fromString(String preference) {
        for (PreferredContact contact : PreferredContact.values()) {
            if (contact.name().equalsIgnoreCase(preference)) {
                return contact;
            }
        }
        throw new IllegalArgumentException("No contact preference with name " + preference + " found");
    }
}