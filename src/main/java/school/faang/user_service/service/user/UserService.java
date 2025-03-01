package school.faang.user_service.service.user;

import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(long userId);

    List<User> getUsersByIds(List<Long> ids);
}
