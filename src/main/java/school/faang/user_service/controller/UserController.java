package school.faang.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;

@RestController
public class UserController {

    @GetMapping("/user/{userId}")
    UserDto getUser(@PathVariable long userId){

    }
}
