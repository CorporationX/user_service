package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUser(@RequestBody @Valid UserFilterDto filterDto) {
        List<User> premiumUsers = userService.findPremiumUser(filterDto);
        return mapper.toDto(premiumUsers);
    }

    @PostMapping("/{user-id}/upload")
    public UserDto uploadFile(@PathVariable("user-id") Long userId,
                              @RequestBody MultipartFile avatar) {
        User updated = userService.setAvatar(userId, avatar);
        return mapper.toDto(updated);
    }
}
