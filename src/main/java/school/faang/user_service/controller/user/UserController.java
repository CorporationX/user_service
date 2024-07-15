package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.service.UserPremiumService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserPremiumService userPremiumService;
    @PostMapping("/premium")
    public ResponseEntity<List<UserDto>> getListPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto ничего не содержит");
            throw new IllegalArgumentException("userFilterDto ничего не содержит");
        }
        return ResponseEntity.ok(userPremiumService.getPremiumUsers(userFilterDto));
    }
}
