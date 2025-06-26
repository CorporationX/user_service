package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.kafka.UserDtoNotification;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.dto.user.UsersFilterDto;
import school.faang.user_service.dto.user.UsersSortOption;
import school.faang.user_service.service.UserDataProcessingService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDataProcessingService userDataProcessingService;
    private final UserService userService;

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Void> checkUserExists(@PathVariable @NotNull @Positive Long userId) {
        userService.getUserById(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> create(
            @RequestParam("username") String username,
            @RequestParam("country") String country,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
         UserDto user = userService.create(username, country, email, password);
         return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Slice<UserViewDto>> getAllUsers(@RequestParam(name = "active", required = false)
                                                          Boolean active,
                                                          @RequestParam(name = "created_before", required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
                                                          LocalDateTime createdBefore,
                                                          @RequestParam(name = "created_after", required = false)
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
                                                          LocalDateTime createdAfter,
                                                          @RequestParam(name = "page", defaultValue = "0")
                                                          @Min(value = 0)
                                                          Integer page,
                                                          @RequestParam(name = "size", defaultValue = "10")
                                                          @Min(value = 4) @Max(value = 10)
                                                          Integer size,
                                                          @RequestParam(name = "sort", required = false)
                                                          UsersSortOption sort,
                                                          @PathVariable(name = "id") Long id) {
        UsersFilterDto usersFilterDto = new UsersFilterDto(active, createdBefore, createdAfter, page, size, sort);
        return new ResponseEntity<>(userService.getAllUsers(usersFilterDto, id), HttpStatus.OK);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.info("No user IDs provided. Returning empty list.");
            return Collections.emptyList();
        }
        log.info("Received request to fetch users with IDs: {}", ids);
        return userDataProcessingService.fetchUsers(ids, 0, ids.size());
    }

    @GetMapping("/users/{id}")
    public UserDtoNotification getUserDtoNotification(@PathVariable(name = "id") long userId) {
        return userDataProcessingService.fetchUserById(userId);
    }
}
