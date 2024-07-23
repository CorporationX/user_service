package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.dto.userdto.UserDto;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/{userId}",produces = "application/json")
    public UserDto getUserById(@PathVariable Long userId) {
        System.out.println("id is "+userId);
        if (userId == null) {
            throw new DataValidationException("userId is null");
        }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId){

        return userService.getUserById(userId);
    }
}
