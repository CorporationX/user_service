package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/CSV")
    public List<UserDto> createUsersCSV(@RequestParam MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            return userService.createUserCSV(inputStream);
        } catch (IOException e) {
            throw new FileException("Can't read file: " + e.getMessage());
        }
    }
}