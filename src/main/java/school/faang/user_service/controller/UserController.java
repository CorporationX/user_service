package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserDtoById(id);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PutMapping("/deactivate")
    public UserDto deactivateUser() {
        return userService.deactivateUser();
    }

    @PostMapping("/upload/csv")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserDto> uploadCsvUsers(@RequestParam("file") MultipartFile csvFile) {
        return userService.uploadCsvUsers(csvFile);
    }

    @PostMapping("/create")
    public UserDto createUser(@RequestBody CreateUserDto createUserDto){
        return userService.createUser(createUserDto);
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<String> getAvatar(@PathVariable @Min(0) long userId){
        String avatarUrl = userService.getAvatarUrl(userId);

        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/findByPhone/{phone}")
    public Long findUserByPhone(@PathVariable String phone){
        return userService.findUserByPhone(phone);
    }
}
