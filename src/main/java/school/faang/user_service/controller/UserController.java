package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping("/{username}/avatar")
    public ResponseEntity<String> updateAvatar(String username) {
        String updatedAvatarUrl = userService.updateAvatar(username);
        return ResponseEntity.ok(updatedAvatarUrl);
    }
}