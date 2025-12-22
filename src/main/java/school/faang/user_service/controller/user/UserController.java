package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserViewDto create(@Valid @RequestBody UserCreateDto userDto) {
        return service.create(userDto);
    }

    @PutMapping("/{userId}")
    public UserViewDto update(@PathVariable Long userId,
                              @Valid @RequestBody UserUpdateDto userDto) {
        return service.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserViewDto getById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @PostMapping("/batch")
    public List<UserViewDto> getByIds(@RequestBody List<Long> userIds) {
        return service.getByIds(userIds);
    }

    @GetMapping("/ids/active")
    public ResponseEntity<List<Long>> getIdsActiveUsers(@RequestParam(defaultValue = "10000") int limit) {
        return ResponseEntity.ok(service.getIdsActiveUsers(limit));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Long>> getAllUsers(@RequestParam(defaultValue = "100", required = false) Integer limit) {
        return ResponseEntity.ok(service.getAllUsers(limit));
    }

}
