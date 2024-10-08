package school.faang.user_service.config.redis.user;

import school.faang.user_service.dto.user.ProfileViewEventDto;

public interface ProfileViewEventPublisher {
    void publish(ProfileViewEventDto message);
}
