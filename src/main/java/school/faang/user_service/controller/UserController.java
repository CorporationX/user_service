package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Deactivate User API", description = "API for managing deactivate users")
public class UserController {
    private static final String MESSAGE_INVALID_ID = "userId cannot be less than zero";
    private final UserService service;

    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate User Profile", description = "Deactivates the profile of a user identified by their user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found")

    })
    public UserDto deactivatesUserProfile(@PathVariable @Parameter(description = "ID of the user to deactivate") Long userId) {
        if (userId < 0) {
            throw new RuntimeException(MESSAGE_INVALID_ID);
        }
        return service.deactivatesUserProfile(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return service.getUser(userId);
    }

    @GetMapping("/list")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return service.getUsersByIds(ids);
    }

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return service.getPremiumUsers(userFilterDto);
    }

    @GetMapping()
    public List<UserDto> getAllUsers(){
        return service.getAllUsers();
    }
}