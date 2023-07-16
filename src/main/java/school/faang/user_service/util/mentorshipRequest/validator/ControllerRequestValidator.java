package school.faang.user_service.util.mentorshipRequest.validator;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.util.mentorshipRequest.exception.IncorrectIdException;
import school.faang.user_service.util.mentorshipRequest.exception.NoRequestsException;
import school.faang.user_service.util.mentorshipRequest.exception.UnknownRejectionReasonException;

import java.util.List;

@Component
public class ControllerRequestValidator {
    @SneakyThrows
    public void validateRequest(BindingResult bindingResult, RuntimeException e) {
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
    public void validateRequest(long id, BindingResult bindingResult, RuntimeException e) {
        validateRequest(id);

        validateRequest(bindingResult, e);
    }

    public void validateRequest(long id) {
        if (id < 1) {
            throw new IncorrectIdException();
        }
    }
}
