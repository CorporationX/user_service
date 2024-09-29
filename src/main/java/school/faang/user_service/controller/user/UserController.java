package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUser(@RequestBody @Valid UserFilterDto filterDto) {
        List<User> premiumUsers = userService.findPremiumUser(filterDto);
        return mapper.toDto(premiumUsers);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Valid long userId) {
        return userService.getUser(userId);
    }

    @PostMapping()
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids){
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/{user-id}/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProfileImage(@PathVariable("user-id") Long userId, @RequestBody MultipartFile file) throws IOException {
        userService.addProfileImage(userId, file);
    }

    @PutMapping("/{user-id}/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileImage(@PathVariable("user-id") Long userId) {
        userService.deleteProfileImage(userId);
    }

    @GetMapping("/{user-id}/profile-image")
    public ResponseEntity<byte[]> getBigImageFromProfile(@PathVariable("user-id") Long userId) {
        byte[] imageBytes = null;
        try {
            imageBytes = userService.getBigImageFromProfile(userId).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/{user-id}/profile-small-image")
    public ResponseEntity<byte[]> getSmallImageFromProfile(@PathVariable("user-id") Long userId) {
        byte[] imageBytes = null;
        try {
            imageBytes = userService.getSmallImageFromProfile(userId).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
