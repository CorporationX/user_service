package school.faang.user_service.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.DeactivateResponseDto;
import school.faang.user_service.dto.contact.ExtendedContactDto;
import school.faang.user_service.dto.contact.TgContactDto;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.PreferenceService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final PreferenceService preferenceService;

    @PostMapping
    public UserDto signup(@RequestBody UserDto userDto) {
        return userService.signup(userDto);
    }

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId) throws JsonProcessingException {
        return userService.getUserWithPublishProfileViewEvent(userId);
    }

    @GetMapping("/user/{userId}")
    UserDto getUserNoPublish(@PathVariable long userId){
        return userService.getUser(userId);
    }

    @GetMapping("/premium-users")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }

    @GetMapping("/get-by-ids")
    List<UserDto> getUsersByIds(@NotEmpty(message = "ids cannot be empty") List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/send-a-file")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("empty file");
        }

        try {
            userService.registerAnArrayOfUser(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("recording error, repeat request");
        }
    }

    @PostMapping("/deactivation/{userId}")
    public DeactivateResponseDto deactivating(@PathVariable long userId) {
        return userService.deactivateUser(userId);
    }

    @PostMapping("/contacts")
    public void updateUserContact(@RequestBody TgContactDto tgContactDto) {
        userService.updateUserContact(tgContactDto);
    }

    @GetMapping("/{userId}/contacts")
    public ExtendedContactDto getUserContact(@PathVariable Long userId) {
        return userService.getUserContact(userId);
    }

    @GetMapping("/get-by-phone")
    public Long findUserIdByPhoneNumber(@RequestParam(name = "phone") String phoneNumber) {
        return userService.findUserIdByPhoneNumber(phoneNumber);
    }

    @GetMapping("/preference{id}")
    public List<Long> getPreference(@PathVariable long id) {
        return preferenceService.getPreference(id);
    }

    @GetMapping("/exists/{id}")
    public Boolean checkUserExist(@PathVariable long id) {
        return userService.checkUserExist(id);
    }
}
