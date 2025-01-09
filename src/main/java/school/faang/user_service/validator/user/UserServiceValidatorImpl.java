package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceValidatorImpl implements UserServiceValidator {

    private final UserRepository userRepository;

    @Override
    public void existsById(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new DataValidationException(String.format("user not found by id: " + userId));
        }
    }
}
