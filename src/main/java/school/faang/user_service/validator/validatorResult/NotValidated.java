package school.faang.user_service.validator.validatorResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class NotValidated extends ValidationResult {
    private final String message;
}
