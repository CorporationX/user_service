package school.faang.user_service.service;

import school.faang.user_service.entity.User;

import java.util.Optional;

public interface UserService {

    void deactivateUser(Long userId);

    Optional<User> findUserById(long userId);

}
