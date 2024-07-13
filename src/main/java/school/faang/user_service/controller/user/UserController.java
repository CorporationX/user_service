package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.service.UserPremiumService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserPremiumService userPremiumService;

    public List<UserDto> getListPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto ничего не содержит");
            throw new IllegalArgumentException("userFilterDto ничего не содержит");
        }
        return userPremiumService.getPremiumUsers(userFilterDto);
    }
}
