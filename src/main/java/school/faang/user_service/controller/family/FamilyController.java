package school.faang.user_service.controller.family;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FamilyController {

    private final UserMapper userMapper;

    public List<User> create(User user) {
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
