package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.userService.UserService;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/random-avatar")
    @ResponseStatus(HttpStatus.OK)
    public String generateRandomAvatar(@PathVariable Long userId) {
        return userService.generateRandomAvatar(userId);
    }

    @GetMapping("{userId}/random-avatar")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getAssignedRandomAvatar(@PathVariable Long userId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("image/svg+xml"));
        return userService.getUserRandomAvatar(userId);
    }
}
