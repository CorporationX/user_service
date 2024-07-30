package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) throws IOException, TranscoderException {
        return userService.createUser(userDto);
    }
}
