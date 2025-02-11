package school.faang.user_service.controller.user;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
    if (userFilterDto == null) {
      return userService.getPremiumUsers();
    }
    return userService.getPremiumUsers(userFilterDto);
  }

  @GetMapping("/user/{userId}")
  public UserDto getUser(@PathVariable Long userId){
    UserDto user = userMapper.toDto(userService.getUser(userId));
    return user;
  }
}
