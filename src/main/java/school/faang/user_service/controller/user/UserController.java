package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject @NotNull UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
    @PutMapping(value = "/{user_id}")
    public ResponseEntity<String> deactivateUserProfile(@PathVariable("user_id") @Positive long id) {
        userService.deactivateUserProfile(id);
        return ResponseEntity.ok("{\n\"message\": \"User deactivated successfully\"\n}");
    }

    @GetMapping("/{user_id}")
    public UserDto getUser(@PathVariable("user_id") @Positive(message = "Id can't be least 1") long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam("user_id") @NotNull @NotEmpty List<Long> userIds) {
        return userService.getUsersByIds(userIds);
    }

    @PostMapping("/csvfile")
    public ResponseEntity<String> addUsersFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        userService.addUsersFromFile(file.getInputStream());
        return ResponseEntity.ok("sucessfuly uploaded");
    }
}
