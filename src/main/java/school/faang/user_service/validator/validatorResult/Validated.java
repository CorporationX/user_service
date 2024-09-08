package school.faang.user_service.validator.validatorResult;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Validated<T> extends ValidationResult<T> {
    private final T value;
}
