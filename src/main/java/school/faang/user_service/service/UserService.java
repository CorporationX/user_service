package school.faang.user_service.service;

import school.faang.user_service.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserById(long userId);

    void banUser(long userId);
}
