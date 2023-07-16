package school.faang.user_service.controller.family;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.event.EventMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FamilyController {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public List<UserDto> create(UserDto user) {
        eventMapper.toEntity(new EventDto());
        return List.of(user);
    }

    private boolean validateFamily(User user) {
        if (user.getCountry().getTitle().equals("USA")) {
            return false;
        }
        if (user.getCountry().getTitle().equals("Russia")) {
            return false;
        }
        return true;
    }

    public List<User> getFamily(long familyId) {
        return new ArrayList<>();
    }
}
