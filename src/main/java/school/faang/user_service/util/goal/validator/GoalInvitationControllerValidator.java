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

    @SneakyThrows
    public void validateInvitation(GoalInvitationDto goalInvitationDto, RuntimeException e) {
        bindingResult = new BeanPropertyBindingResult(goalInvitationDto, "goalInvitationDto");

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder message = new StringBuilder();

            fieldErrors.forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw e.getClass().getConstructor(String.class).newInstance(message.toString());
        }
    }

    @SneakyThrows
    public void validateInvitation(InvitationFilterDto invitationFilterDto, RuntimeException e) {
        validateStatusOfInvitation(invitationFilterDto);

        bindingResult = new BeanPropertyBindingResult(invitationFilterDto, "goalInvitationDto");

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder message = new StringBuilder();

            fieldErrors.forEach(fieldError -> {
                message.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            });

            throw e.getClass().getConstructor(String.class).newInstance(message.toString());
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
