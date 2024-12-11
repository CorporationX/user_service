package school.faang.user_service.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.Language;

public record UserForNotificationDto(
        @JsonProperty("id")
        long id,

        @JsonProperty("username")
        String username,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("language")
        Language language,

        @JsonProperty("preference")
        PreferredContact preference
) {
}
