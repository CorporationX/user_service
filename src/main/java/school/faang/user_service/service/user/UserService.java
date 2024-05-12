package school.faang.user_service.service.user;

import school.faang.user_service.entity.User;

public interface UserService {

    User findById(Long id);

    void deactivateUserById(Long id);
}
