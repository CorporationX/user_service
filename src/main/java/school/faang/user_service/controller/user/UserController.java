package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.user.UserService;

/**
 * Контроллер отвечающий за обработку запросов пользователя для управления пользователями.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USERS)
public class UserController {

  private final UserService userService;

  @GetMapping("/user/{userId}/deactivate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User deactivation success"),
      @ApiResponse(responseCode = "404", description = "User deactivation failed")
  })
  public ResponseEntity<UserDto> deactivateUser(@PathVariable("userId") long userId) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.deactivateUser(userId));
  }

}
