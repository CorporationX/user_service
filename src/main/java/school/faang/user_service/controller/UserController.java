package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable @NotNull @Positive long userId) {
        DeactivatedUserDto deactivatedUser = userService.deactivateUser(userId);
        return ResponseEntity.ok(deactivatedUser);
    }
}