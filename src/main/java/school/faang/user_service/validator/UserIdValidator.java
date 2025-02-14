package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserIdValidator {
    public void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be blank");
        } else if(id <= 0)
            throw new IllegalArgumentException("Id must be positive");
    }
}
