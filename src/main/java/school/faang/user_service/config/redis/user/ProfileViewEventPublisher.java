package school.faang.user_service.config.redis.user;

import school.faang.user_service.dto.user.ProfileViewEventDto;

import java.util.List;

public interface ProfileViewEventPublisher {
    void publish(List<ProfileViewEventDto> message);
}
