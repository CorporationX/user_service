package school.faang.user_service.controller.user;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@NonNull @RequestBody List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        return userService.getUsersByIds(ids);
    }
}

