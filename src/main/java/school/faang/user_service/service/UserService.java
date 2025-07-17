package school.faang.user_service.service;

import school.faang.user_service.entity.User;

public interface UserService {
    User getUserEntityById(Long userId);
}
