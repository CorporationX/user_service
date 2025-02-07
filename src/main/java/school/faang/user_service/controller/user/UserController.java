package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserIdsDto;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.dto.user_jira.UserJiraCreateUpdateDto;
import school.faang.user_service.dto.user_jira.UserJiraDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Tag(
        name = "Users",
        description = "API for managing user accounts, including user details, subscriptions, skills, and goals."
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AvatarService avatarService;
    private final UserProfilePicMapper userProfilePicMapper;

    @Operation(summary = "Get user information by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found successfully"),
            @ApiResponse(responseCode = "404", description = "User was not found")
    })
    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @Operation(summary = "Get users' information by theirs ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/by-ids")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody @Valid UserIdsDto request) {
        List<UserDto> users = userService.getUsers(request.getUserIds());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get IDs of non-existing users from provided list",
            description = "Returns a list of user IDs from the provided list that are not present in the database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of non-existing user IDs"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/not-existing-ids")
    public ResponseEntity<List<Long>> getNotExistingUserIds(@RequestBody @Valid UserIdsDto request) {
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(request.getUserIds());
        return ResponseEntity.ok(notExistingUserIds);
    }

    @PostMapping("/save")
    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto userDto) {
        UserDto savedUser = userService.saveUser(userDto);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/not-premium")
    public List<UserDto> getNotPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getNotPremiumUsers(filters);
    }

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserProfilePicDto> uploadUserAvatar(@PathVariable Long userId,
                                                              @RequestParam(value = "file", required = false) MultipartFile file) {
        UserProfilePic userProfilePic = avatarService.uploadUserAvatar(userId, file);
        return ResponseEntity.ok(userProfilePicMapper.toDto(userProfilePic));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable Long userId) {
        byte[] avatarData = avatarService.getOriginalUserAvatar(userId);
        ByteArrayResource resource = new ByteArrayResource(avatarData);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(avatarData.length)
                .body(resource);
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable Long userId) {
        avatarService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add Jira account information for a user",
            description = "Adds or updates the Jira account details for the specified user, including account ID, email, and API token, for a given Jira domain."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated information about Jira user account"),
            @ApiResponse(responseCode = "201", description = "Successfully saved information about Jira user account"),
            @ApiResponse(responseCode = "404", description = "User with given user ID not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{userId}/jira/{jiraDomain}")
    public ResponseEntity<UserJiraDto> saveOrUpdateUserJiraInfo(
            @PathVariable @Min(1) long userId,
            @PathVariable @NotBlank @Length(min = 1, max = 64) String jiraDomain,
            @Valid @RequestBody UserJiraCreateUpdateDto createUpdateDto) {
        UserJiraDto responseDto = userService.saveOrUpdateUserJiraInfo(userId, jiraDomain, createUpdateDto);
        return responseDto.getCreatedAt().equals(responseDto.getUpdatedAt())
                ? ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
                : ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully parsed file data"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/parser")
    public List<UserDto> parsePersonDataIntoUserDto(@RequestParam("file") MultipartFile csvFile) {
        return userService.parsePersonDataIntoUserDto(csvFile);
    }

    @Operation(
            summary = "Get information of user Jira account by given domain",
            description = "Returns basic information about user Jira account including accountId, token, and email for a specified domain."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user Jira account information"),
            @ApiResponse(responseCode = "404", description = "User Jira account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/{userId}/jira/{jiraDomain}")
    public ResponseEntity<UserJiraDto> getUserJiraInfo(
            @PathVariable @Min(1) long userId,
            @PathVariable @NotBlank @Length(min = 1, max = 64) String jiraDomain) {
        UserJiraDto responseDto = userService.getUserJiraInfo(userId, jiraDomain);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}/profile")
    public UserDto getUserProfile(
            @PathVariable @Min(1) Long userId) {
        return userService.getUser(userId);
    }

}
