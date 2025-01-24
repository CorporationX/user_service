package school.faang.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.AvatarDto;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{username}/avatar")
    public ResponseEntity<AvatarDto> updateAvatar(String username) {
        AvatarDto updatedAvatar = userService.updateAvatar(username);
        return ResponseEntity.ok(updatedAvatar);
    }
}