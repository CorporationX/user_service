package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;

@Component
@RequiredArgsConstructor
public class UserFilterDtoValidator {
    public void checkIsNull(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            throw new IllegalArgumentException("Аргумент метода getPremiumUsers не может быть null");
        }
    }
}