package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;


}
