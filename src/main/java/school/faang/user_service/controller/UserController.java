package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.DeactivateResponseDto;
import school.faang.user_service.service.UserService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService deactivatingService;

    @PostMapping("/deactivation/{userId}")
    public DeactivateResponseDto deactivating(@PathVariable @Min(0) long userId) {
        return deactivatingService.deactivateUser(userId);
    }
}

