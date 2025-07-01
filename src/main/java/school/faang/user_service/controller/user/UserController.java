package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserFilterRequestDto;
import school.faang.user_service.dto.user.UserNotificationResponseDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.facade.user.UserFacade;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserFacade userFacade;

    @PostMapping("/registration")
    public ResponseEntity<UserRegisterResponseDto> registrationUser
            (@RequestBody @Valid UserRegisterRequestDto userRegisterRequestDto) {
        log.debug("User controller accepted request registration user {}", userRegisterRequestDto);

        UserRegisterResponseDto response = userFacade.registrationUser(userRegisterRequestDto);
        log.debug("User controller return response registration user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.info("User controller accepted request get current user");

        UserResponseDto response = userFacade.getCurrentUser();
        log.info("User controller return response get current user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserByIdOrThrow(@PathVariable long userId) {
        log.info("User controller accepted request get user with id {}", userId);

        UserResponseDto response = userFacade.getUserByIdOrThrow(userId);
        log.info("User controller return response get user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/notification")
    public ResponseEntity<UserNotificationResponseDto> getNotificationUserById(@PathVariable long userId) {
        log.debug("User controller accepted request get user with id {}", userId);

        UserNotificationResponseDto response = userFacade.getNotificationUserById(userId);
        log.debug("User controller return response get user {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsersByIds(@RequestParam List<Long> userIds) {
        log.info("User controller accepted request get users with ids {}", userIds);

        List<UserResponseDto> response = userFacade.getUsersByIds(userIds);
        log.info("User controller return response get users {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<List<UserNotificationResponseDto>> getNotificationUserByIds(@RequestParam List<Long> userIds) {
        log.debug("User controller accepted request get users with ids {}", userIds);

        List<UserNotificationResponseDto> response = userFacade.getNotificationUserByIds(userIds);
        log.debug("User controller return response get users {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<UserResponseDto>> getUserByFilter(@RequestBody UserFilterRequestDto filterDto) {
        log.debug("User controller accepted request get users by filter {}", filterDto);

        List<UserResponseDto> response = userFacade.filter(filterDto);
        log.debug("User controller return response get users by filter {}", response);
        return ResponseEntity.ok(response);
    }
}
