package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.dto.user.UsersFilterDto;
import school.faang.user_service.dto.user.UsersSortOption;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Void> checkUserExists(@PathVariable @NotNull @Positive Long userId) {
        userService.getUserById(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<UserViewDto>> getAllUsers(@RequestParam(name = "active", required = false)
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
        log.info("Request received: method=GET, URI=/users. Параметры active={}, createdBefore={}, createdAfter={}, " +
                "page={}, size={}, sort option = {}", active, createdBefore, createdAfter, page, size, sort);
        UsersFilterDto usersFilterDto = new UsersFilterDto(active, createdBefore, createdAfter, page, size, sort);
        return new ResponseEntity<>(userService.getAllUsers(usersFilterDto, id), HttpStatus.OK);
    }
}