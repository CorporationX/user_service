package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final MentorshipService mentorshipService;
    private final EventService eventService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto,
                              @RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        return userService.createUser(userDto, multipartFile);
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/regular")
    public List<UserDto> getRegularUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getRegularUsers(userFilterDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Long id) {
        return userService.existsById(id);
    }

    @GetMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getAvatar(@PathVariable Long userId) {
        return userService.getAvatar(userId);
    }

    @PostMapping("/deactivate/{userId}")
    public void deactivateProfile(@PathVariable long userId) {
        userService.deactivateUser(userId);
        mentorshipService.deactivateMentorship(userId);
        eventService.deleteEventByUserId(userId);
    }
}