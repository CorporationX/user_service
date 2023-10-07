package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ResponseDeactivateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.service.user.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserContext userContext;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/students/upload")
    public void uploadStudents(@RequestParam("students") MultipartFile students) {
        log.debug("Received request to upload students to the database from file: {}", students.getName());
        userService.saveStudents(students);
    }

    @PutMapping("/profile_pic/upload")
    public UserProfilePicDto saveProfilePic(@RequestParam("picture") MultipartFile profilePic) {
        Long userId = userContext.getUserId();
        log.debug("Received request to upload profile picture for user with id: {} from file: {}",
                userId, profilePic.getName());
        return userService.saveProfilePic(profilePic, userId);
    }

    @GetMapping(value = "/profile_pic",  produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getProfilePic() {
        Long userId = userContext.getUserId();
        log.debug("Received request to get profile picture for user with id: {}", userId);
        InputStream responseBody = userService.getProfilePic(userId);
        return ResponseEntity.ok().body(new InputStreamResource(responseBody));
    }

    @DeleteMapping("/profile_pic")
    public ResponseEntity<String> deleteProfilePic() {
        Long userId = userContext.getUserId();
        log.debug("Received request to delete profile picture for user with id: {}", userId);
        userService.deleteProfilePic(userId);
        return ResponseEntity.ok().body("Profile picture has been successfully deleted");
    }

    @GetMapping("/profile_pic/view") // for test request
    public ResponseEntity<byte[]> viewProfilePic() {
        Long userId = userContext.getUserId();
        log.debug("Received request to get profile picture for user with id: {}", userId);
        byte[] responseBody;
        try {
            responseBody = userService.getProfilePic(userId).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

    @PostMapping("deactivate/{id}")
    public ResponseDeactivateDto deactivateUser(@PathVariable @Min(0) Long id) {
        return userService.deactivateUser(id);
    }
}
