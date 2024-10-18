package school.faang.user_service.mapper.follower;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.follower.FollowerEventDto;
import school.faang.user_service.entity.User;

@Component
public class FollowerEventMapper {
    public FollowerEventDto toEventDto(User user, long followeeId) {
        return FollowerEventDto.builder()
                .username(user.getUsername())
                .followerId(user.getId())
                .followeeId(followeeId)
                .build();
    }
}
