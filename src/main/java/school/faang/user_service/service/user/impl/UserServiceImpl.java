package school.faang.user_service.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.adapter.user.UserRepositoryAdapter;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryAdapter userRepositoryAdapter;

    @Override
    public User getUserById(long userId) {
        return userRepositoryAdapter.getUserById(userId);
    }
}
