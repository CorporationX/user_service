package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, S3Service s3Service) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.s3Service = s3Service;
    }

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userMapper.toDto(userService.findUserById(userId));
    }

    @PostMapping("/users/{userId}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(s3Service.uploadAvatar(userId, file), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/avatar")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long userId) {
        byte[] data = s3Service.downloadAvatar(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);

    }

    @DeleteMapping("/users/{userId}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long userId) {
        return new ResponseEntity<>(s3Service.deleteAvatar(userId), HttpStatus.OK);
    }
}
