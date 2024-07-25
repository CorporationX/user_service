package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping("/new")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                              @RequestPart(value = "userJson") String userJson) throws JsonProcessingException {
        UserDto userDto = objectMapper.readValue(userJson, UserDto.class);
        return userService.createUser(userDto, multipartFile);
    }

    @PutMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUserAvatar(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                 @PathVariable Long userId) {
        userService.updateUserAvatar(userId, multipartFile);
    }
}
