package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.user.UserValidator;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.validator.file.FileValidator;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final UserValidator userValidator;


    public UserDto deactivateUser(long userId) {
        userValidator.validateUserId(userId);
        UserDto user = userService.deactivate(userId);
        userService.removeMenteeAndGoals(userId);
        return user;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userMapper.toDto(userService.findUserById(userId));
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(s3Service.uploadAvatar(userId, file), HttpStatus.OK);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long userId) {
        byte[] data = s3Service.downloadAvatar(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);

    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long userId) {
        return new ResponseEntity<>(s3Service.deleteAvatar(userId), HttpStatus.OK);
    }
    private final FileValidator fileValidator;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) throws IOException, TranscoderException {
        return userService.createUser(userDto);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) throws IOException {
        fileValidator.validateFile(file);
        userService.processCSVAsync(file.getInputStream());
        return ResponseEntity.ok("The file has been uploaded successfully and will be processed!");
    }

    @GetMapping
    public List<Long> findAllUserIds() {
        return userService.findAllUserIds();
    }
}
