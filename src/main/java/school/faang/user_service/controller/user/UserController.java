package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.ConfidentialUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/premium")
    public List<UserDto> getPremiumUser(@RequestBody @Valid UserFilterDto filterDto) {
        List<User> premiumUsers = userService.findPremiumUser(filterDto);
        return userMapper.toDto(premiumUsers);
    }
    
    @PostMapping("/register")
    public UserDto registerUser(@RequestBody @Valid ConfidentialUserDto dto) {
        User toRegister = userMapper.toEntity(dto);
        User registered = userService.registerNewUser(toRegister);
        return userMapper.toDto(registered);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Valid long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids){
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/profile-image/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProfileImage(@PathVariable("user-id") Long userId, @RequestBody MultipartFile file) throws IOException {
        userService.addProfileImage(userId, file);
    }

    @DeleteMapping("/profile-image/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileImage(@PathVariable("user-id") Long userId) {
        userService.deleteProfileImage(userId);
    }

    @GetMapping("/profile-image/{user-id}")
    public ResponseEntity<Resource> getBigImageFromProfile(@PathVariable("user-id") Long userId) throws IOException {
        Resource imageResource = userService.getBigImageFromProfile(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "profile.jpg");

        return new ResponseEntity<>(imageResource, headers, HttpStatus.OK);
    }

    @GetMapping("/profile-small-image/{user-id}")
    public ResponseEntity<Resource> getSmallImageFromProfile(@PathVariable("user-id") Long userId) throws IOException {
        Resource imageResource = userService.getSmallImageFromProfile(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentDispositionFormData("inline", "profile-small.jpg");

        return new ResponseEntity<>(imageResource, headers, HttpStatus.OK);
    }
}
