package school.faang.user_service.validator.user;

import school.faang.user_service.entity.User;

import java.util.List;

public interface UserValidator {
    User validateUserExistence(Long userId);

    List<User> validateUsersExistence(List<Long> userIds);

}
