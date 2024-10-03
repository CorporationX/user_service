package school.faang.user_service.repository.user;

import school.faang.user_service.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    void batchInsertUsers(List<User> users);
}
