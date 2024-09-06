package school.faang.user_service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

@Validated
public interface GoalInvitationService {
    GoalInvitationDto createInvitation(@Valid GoalInvitationDto goalInvitationDto);

    GoalInvitationDto acceptGoalInvitation(@PositiveOrZero long id);

    GoalInvitationDto rejectGoalInvitation(@PositiveOrZero long id);

    List<GoalInvitationDto> getInvitations(@Valid InvitationFilterDto invitationFilterDto);

}
