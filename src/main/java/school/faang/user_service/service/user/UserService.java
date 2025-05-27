package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final UserMapper userMapper;

    public UserDto getUserById(@RequestParam long id) {
        return userMapper.toDto(userRepositoryAdapter.findById(id));
    }
}
