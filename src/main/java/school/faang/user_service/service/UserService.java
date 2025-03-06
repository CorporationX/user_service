package school.faang.user_service.service;

import school.faang.user_service.entity.User;

public interface UserService {
    void saveUser(User user);

    User findUserById(Long id);
}
