package school.faang.user_service.mapper;

import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Mapper
@Component
public interface GoalInvitationMapper {
    GoalInvitationMapper INSTANCE = Mappers.getMapper(GoalInvitationMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "invited.id", target = "invitedUserId")
    @Mapping(source = "goal.id", target = "goalId")
    @Mapping(source = "status", target = "status")
    GoalInvitationDto toDto(GoalInvitation goalInvitation);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "inviterId", target = "inviter.id")
    @Mapping(source = "invitedUserId", target = "invited.id")
    @Mapping(source = "goalId", target = "goal.id")
    @Mapping(source = "status", target = "status")
    GoalInvitation toEntity(GoalInvitationDto goalInvitationDto);
}
