package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.auth.AuthRequest;
import school.faang.user_service.dto.auth.AuthResponse;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.service.auth.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Аутентификация пользователей")
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody
            CreateUserDto dto
    ) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("authentication")
    public ResponseEntity<AuthResponse> register(
            @RequestBody
            AuthRequest dto
    ) {
        return ResponseEntity.ok(authService.authenticate(dto));
    }
}
