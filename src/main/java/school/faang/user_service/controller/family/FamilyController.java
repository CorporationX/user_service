package school.faang.user_service.controller.family;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FamilyController {

    private final UserMapper userMapper;

    public List<User> create(UserDto userDto) {
        return List.of(userMapper.toEntity(userDto));
    }
}
