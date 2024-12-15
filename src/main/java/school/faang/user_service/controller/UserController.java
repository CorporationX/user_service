package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserContactsDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsResponseDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.CsvFile;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable
            @Positive(message = "User id must be a positive number")
            long userId
    ) {
        return ResponseEntity.ok(userService.findUserDtoById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> deactivateProfile(
            @PathVariable
            @Positive(message = "User id must be a positive number")
            long userId
    ) {
        return ResponseEntity.ok(userService.deactivateProfile(userId));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of users with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public List<UserDto> getAllUsers(@Valid UserFilterDto filter) {
        return userService.getAllUsers(filter);
    }

    @GetMapping("/premium")
    @Operation(summary = "Get premium users", description = "Retrieve a list of premium users with optional filtering and pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list premium users")
    public List<UserDto> getPremiumUsers(@Valid UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }

    @GetMapping("/ids")
    public List<UserDto> getUsersByIds(@RequestParam List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/upload")
    public ResponseEntity<ProcessResultDto> uploadToCsv(@RequestParam("file") @CsvFile MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        long fileSize = file.getSize();

        log.info("Received file: name = {}, size = {} bytes", filename, fileSize);

        ProcessResultDto result = userService.importUsersFromCsv(file.getInputStream());

        log.info("File '{}' uploaded successfully. Processed {} records with {} errors.",
                filename, result.getCountSuccessfullySavedUsers(), result.getErrors().size());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/profile-settings")
    public ResponseEntity<UserProfileSettingsResponseDto> saveProfileSettings(
            @PathVariable
            @Positive(message = "User id must be a positive number")
            Long userId,
            @RequestBody @Valid UserProfileSettingsDto userProfileSettingsDto
    ) {
        return ResponseEntity.ok(userService.saveProfileSettings(userId, userProfileSettingsDto));
    }

    @GetMapping("/{userId}/profile-settings")
    public ResponseEntity<UserProfileSettingsResponseDto> getProfileSettings(
            @PathVariable
            @Positive(message = "User id must be a positive number")
            Long userId
    ) {
        return ResponseEntity.ok(userService.getProfileSettings(userId));
    }

    @GetMapping("/{userId}/contacts")
    @Operation(summary = "Get contacts of a user", description = "Retrieve a list of contact preferences of a user ")
    public ResponseEntity<UserContactsDto> getUserContacts(
            @PathVariable @Positive(message = "User id should be a positive integer") Long userId) {
        log.info("Getting contacts of user with id {}", userId);
        return ResponseEntity.ok(userService.getUserContacts(userId));
    }

    @PutMapping("/{userId}/contact-preference")
    @Operation(summary = "Update user's preferred contact method", description = "Update the preferred contact method of a user by ID")
    @ApiResponse(responseCode = "200", description = "Preferred contact updated successfully")
    public ResponseEntity<UserContactsDto> updateUserPreference(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @RequestParam("preference") @NotNull(message = "Preference must be provided") PreferredContact preference,
            @RequestHeader("Current-User-Id") @NotNull(message = "Current User ID must be provided") Long currentUserId
    ) {
        UserContactsDto updatedUser = userService.updateUserPreferredContact(userId, preference, currentUserId);
        return ResponseEntity.ok(updatedUser);
    }
}
