package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivatedUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }
}
