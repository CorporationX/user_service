package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserValidator userValidator;
    private final UserService user_Service;

    public UserDto deactivateUser(long userId) {
        userValidator.validateUserId(userId);
        UserDto user = user_Service.deactivate(userId);
        user_Service.removeMenteeAndGoals(userId);
        return user;
    }

}
