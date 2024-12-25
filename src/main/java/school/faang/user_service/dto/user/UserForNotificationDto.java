package school.faang.user_service.dto.user;

import school.faang.user_service.model.contact.PreferredContact;
import school.faang.user_service.model.recommendation.Language;

public record UserForNotificationDto(
        long id,
        String username,
        String email,
        String phone,
        Language locale,
        PreferredContact preference
) {
}
