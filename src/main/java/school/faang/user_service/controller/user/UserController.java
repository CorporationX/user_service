package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.avatar.AvatarType;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserAvatarService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.image.ValidImage;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserAvatarService userAvatarService;
    private final UserContext userContext;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        User user = userService.getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userMapper.toUserDtoList(userService.getAllUsers());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateTelegramUserId")
    UserDto updateTelegramUserId(@RequestParam String telegramUsername,
                                 @RequestParam String telegramChatId) {
        User user = userService.updateTelegramData(telegramUsername, telegramChatId);
        return userMapper.toDto(user);
    }

    @PostMapping("/list")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<UserDto> users) {
        List<User> userList = userMapper.toUserList(users);
        userList = userService.getUsersByIds(userList);
        List<UserDto> userDtoList = userMapper.toUserDtoList(userList);
        return ResponseEntity.ok(userDtoList);
    }

    @PostMapping("/list-by-ids")
    public ResponseEntity<List<UserDto>> getUsersByIdsByIdList(@RequestBody List<Long> ids) {
        List<User> users = userService.getUsersByIdList(ids);
        return ResponseEntity.ok(userMapper.toUserDtoList(users));
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(@RequestParam("userId") Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDto> registerUser(@RequestBody @Validated UserRegistrationDto userRegistrationDto) {

        User registeredUser = userService.registerUser(
                userRegistrationDto.getUsername(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getPassword(),
                userRegistrationDto.getCountryId(),
                userRegistrationDto.getTelegramUsername()
        );

        UserRegistrationDto responseDto = userMapper.toRegistrationDto(registeredUser);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/avatar/generate")
    public ResponseEntity<String> generateAvatarForUser(@RequestParam AvatarType type) {
        long userId = userService.getCurrentUserId();
        User user = userService.getUser(userId);
        userAvatarService.generateAvatarForNewUser(user, type);
        return ResponseEntity.ok("Avatar generated successfully.");
    }

    @GetMapping("/avatar/url")
    public ResponseEntity<String> getUserAvatar() {
        long userId = userService.getCurrentUserId();
        User user = userService.getUser(userId);
        String avatarUrl = userAvatarService.getUserAvatar(user).toString();
        return ResponseEntity.ok(avatarUrl);
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(
            @ValidImage @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "small") String size) {
        String avatarUrl = userService.uploadAvatar(file, size);
        return ResponseEntity.ok(avatarUrl);
    }

    @GetMapping("/avatar")
    public ResponseEntity<String> downloadAvatar(@RequestParam(defaultValue = "small") String size) {
        String avatarUrl = userService.downloadAvatar(size);
        return ResponseEntity.ok(avatarUrl);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<Void> deleteAvatar() {
        userService.deleteAvatar();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/page")
    public Page<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids, Pageable pageable) {
        return userService.getUsersByIds(ids, pageable);
    }
}
