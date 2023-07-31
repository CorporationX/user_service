package school.faang.user_service.util.goal.validator;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.util.goal.exception.IncorrectIdException;
import school.faang.user_service.util.goal.exception.IncorrectStatusException;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;

import java.util.ArrayList;
import java.util.List;

@Component
public class GoalInvitationControllerValidator {

    private static List<RequestStatus> statuses = new ArrayList<>();
    private BindingResult bindingResult;

    static {
        statuses.add(RequestStatus.PENDING);
        statuses.add(RequestStatus.ACCEPTED);
        statuses.add(RequestStatus.REJECTED);
    }

    public void checkInviterAndInvitedAreTheSame(GoalInvitationDto dto) {
        if (dto.inviterId() == dto.invitedUserId()) {
            throw new MappingGoalInvitationDtoException("Inviter and invited are the same");
        }
    }

    public void validateInvitation(Long id) {
        if (id == null || id < 1) {
            throw new IncorrectIdException("Id can't be lower than 1");
        }
    }

    public void validateStatusOfInvitation(InvitationFilterDto invitationFilterDto) {
        if (invitationFilterDto.status() != null && (!statuses.contains(invitationFilterDto.status()))) {
                throw new IncorrectStatusException("Status must be like " + statuses);
        }
    }
}
