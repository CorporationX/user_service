package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.service.user.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

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
    public UserProfilePicDto uploadProfilePic(@RequestParam("picture") MultipartFile profilePic) {
        Long userId = userContext.getUserId();
        log.debug("Received request to upload profile picture for user with id: {} from file: {}",
                userId, profilePic.getName());
        return userService.uploadProfilePic(profilePic, userId);
    }
}
