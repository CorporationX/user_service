package school.faang.user_service.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import school.faang.user_service.dto.filter.FilterDto;
import school.faang.user_service.validation.NotEmptyFilter;

public class NotEmptyFilterValidator implements ConstraintValidator<NotEmptyFilter, FilterDto> {
    @Override
    public boolean isValid(FilterDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.hasFilterCriteria();
    }
}
