package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/regular")
    public List<UserDto> getRegularUsers(@ParameterObject UserFilterDto userFilterDto) {
        return userService.getRegularUsers(userFilterDto);
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Long id) {
        return userService.existsById(id);
    }

    @PostMapping("/{userId}/random-avatar")
    @ResponseStatus(HttpStatus.OK)
    public String generateRandomAvatar(@PathVariable Long userId) {
        return userService.generateRandomAvatar(userId);
    }

    @GetMapping("{userId}/random-avatar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getAssignedRandomAvatar(@PathVariable Long userId) {
        byte[] userRandomAvatar = userService.getUserRandomAvatar(userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf("image/svg+xml"));
        return new ResponseEntity<>(userRandomAvatar, httpHeaders, HttpStatus.OK);
    }
}