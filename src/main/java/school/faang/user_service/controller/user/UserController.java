package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
@Tag(name = "Users", description = "Interaction with users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Create user",
            description = "Allows you to create a user"
    )
    @PostMapping()
    public UserDto create(@RequestBody @Valid CreateUserDto userDto) {
        return userService.create(userDto);
    }

    @Operation(
            summary = "Updating user data",
            description = "Allows you to update user data"
    )
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody @Valid UpdateUserDto userDto) {
        return userService.update(userId, userDto);
    }

    @PutMapping("/profile")
    public UserDto update(
            @RequestBody
            UpdateUserDto dto
    ) {
        return userService.updateProfile(dto);
    }

    @Operation(
            summary = "Search user by ID",
            description = "Allows you to get a user by their ID"
    )
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @Operation(
            summary = "Search users by IDs",
            description = "Allows you to get a list of users by a list of IDs"
    )
    @PostMapping("/all")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @Operation(

            summary = "Load data from csv file",
            description = "Allows you to download user data from a csv file"
    )
    @PostMapping("/upload-csv")
    public ResponseEntity<?> getUsersByIds(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload the file");
        }
        if (!Objects.requireNonNull(file.getContentType()).contains("text/csv")
                && !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Please upload the CSV file");
        }
        List<UserDto> users = userService.addUsersToFile(file);
        return ResponseEntity.ok("File processed successfully. Number of records: " + users.size());

    @Operation(
            summary = "Deactivate user by ID",
            description = "Allows you to deactivate user by their ID"
    )
    public UserDto deactivateUserById(long userId) {
        return userService.deactivateUserById(userId);
    }
}
