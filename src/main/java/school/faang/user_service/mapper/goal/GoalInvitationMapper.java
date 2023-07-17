package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.goal.GoalInvitationService;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    RequestStatus PENDING = RequestStatus.PENDING;

    @Mapping(target = "inviterId", source = "inviter.id")
    @Mapping(target = "invitedUserId", source = "invited.id")
    @Mapping(target = "goalId", source = "goal.id")
    GoalInvitationDto toDto(GoalInvitation entity);

    @Mapping(target = "inviter", expression = "java(service.findUserById(dto.inviterId()))")
    @Mapping(target = "invited", expression = "java(service.findUserById(dto.invitedUserId()))")
    @Mapping(target = "goal", expression = "java(service.findGoalById(dto.goalId()))")
    @Mapping(target = "status", expression = "java(PENDING)") // по идее статус должен быть такой при создании запроса, я же не могу отправить запрос, который по умолчанию будет принят
    GoalInvitation toEntity(GoalInvitationDto dto, GoalInvitationService service);
}
