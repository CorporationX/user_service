package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    @DeleteMapping("/deactivate")
    public ResponseEntity<String> deactivateUser(@RequestParam("userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new DataValidationException("Invalid user ID.");
        }
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User deactivated successfully.");
    }
}