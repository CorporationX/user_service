package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.UserMapper;

@Component
public class GoalInvitationMapper {
    UserMapper userMapper;

    public GoalInvitationDto toGoalInvitationDto(GoalInvitation saved) {
        if (saved == null) {
            return null;
        }

        return new GoalInvitationDto(saved.getId(),
                userMapper.toUserDto(saved.getInviter()),
                userMapper.toUserDto(saved.getInvited()),
                saved.getStatus());
    }
}
