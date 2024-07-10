package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFilterDtoValidator {
    public void checkIsNull(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto == null");
            throw new IllegalArgumentException("Аргумент метода getPremiumUsers не может быть null");
        }
    }

    public void checkUserFilterDtoIsNull(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            throw new DataValidationException("userFilterDto не должен иметь значение null");
        }
    }
}