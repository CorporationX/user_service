package school.faang.user_service.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserValidator {

    public void validateCreateUser(UserDto user) {
        validationNotNullNotEmpty(user.getUsername(), "логин");
        validationNotNullNotEmpty(user.getEmail(), "email");
        validationNotNullNotEmpty(user.getPassword(), "пароль");
        if (user.getCountryId() == null) {
            throw new DataValidationException("Введите страну");
        }
    }

    private void validationNotNullNotEmpty(String field, String fieldName) {
        if (StringUtils.isBlank(field)) {
            throw new DataValidationException("Введите " + fieldName);
        }
    }
}