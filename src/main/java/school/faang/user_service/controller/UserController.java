package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.User_Service;
import school.faang.user_service.validator.UserValidator;

@Component
@RequiredArgsConstructor
public class UserController {

    private final UserValidator userValidator;
    private final User_Service user_Service;

    public UserDto deactivateUser(long userId) {
        userValidator.validateUserId(userId);
        UserDto user = user_Service.deactivate(userId);
        user_Service.removeMenteeAndGoals(userId);
        return user;
    }

}
