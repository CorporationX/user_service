package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.event.follower.FollowerEvent;
import school.faang.user_service.publisher.followerevent.FollowerEventPublisher;
import school.faang.user_service.service.user.DeactivationService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final DeactivationService deactivationService;
    private final UserContext userContext;
    private final FollowerEventPublisher publisher;

    @Operation(summary = "Create new user")
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @Operation(summary = "Get premium users with filters")
    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return userService.getPremiumUsers(filters);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Positive(message = "ID can't be less than 1") long userId) {
        return userService.getUser(userId);
    }

    @Operation(summary = "Get user's jira account info")
    @GetMapping("/jira")
    public JiraAccountDto getJiraAccountInfo() {
        long userId = userContext.getUserId();
        return userService.getJiraAccountInfo(userId);
    }

    @Operation(summary = "Get users by their ids")
    @PostMapping("/list")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> usersIds) {
        return userService.getUsersByIds(usersIds);
    }

    @Operation(summary = "Get list of user's followers by userID")
    @GetMapping("/followers")
    public List<UserDto> getFollowers() {
        long userId = userContext.getUserId();
        return userService.getFollowers(userId);
    }

    @Operation(summary = "Save user's Jira account info")
    @PutMapping("/jira")
    public UserDto saveJiraAccountInfo(@RequestBody JiraAccountDto jiraAccountDto) {
        long userId = userContext.getUserId();
        return userService.saveJiraAccountInfo(userId, jiraAccountDto);
    }

    @Operation(summary = "Deactivate user")
    @PutMapping("/deactivated")
    public UserDto deactivateUser() {
        long userId = userContext.getUserId();
        return deactivationService.deactivateUser(userId);
    }

    @GetMapping("/test")
    public void test() {
        publisher.publish(new FollowerEvent(1L, 2L));
    }
}
