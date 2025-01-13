package school.faang.user_service.service.user;


import school.faang.user_service.dto.entity.User;

public interface UserService {
    User getUserById(long userId);
}
