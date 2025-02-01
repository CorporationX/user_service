package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.subscription.SubscriptionDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUser(@NotNull @PathVariable long userId) {
        return ResponseEntity.ok().body(userService.getUserDtoById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> userIds) {
        return ResponseEntity.ok().body(userService.getUserDtosByIds(userIds));
    }

    @GetMapping("/random_avatar")
    public String generateRandomAvatar() {
        return userService.generateRandomAvatar();

    }

    @PutMapping("update-users-rank")
    public ResponseEntity<Void> updateUsersRankByUserIds(@RequestBody @Valid UpdateUsersRankDto usersDto) {
        return userService.updateUsersRankByUserIds(usersDto);
    }

    @PostMapping("file/import")
    @ResponseStatus(HttpStatus.CREATED)
    public void importCSVFile(@RequestParam("file") MultipartFile csvFile) throws IOException {
        userService.uploadUsers(csvFile);
    }

    @PostMapping("/{userId}/deactivate")
    public void deactivate(@NotNull @PathVariable long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("followees")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getUserFollowees() {
        return userService.findUsersFollowees();
    }
}
