package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.userPremium.UserPremiumDto;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.service.userPremium.UserPremiumService;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.csv.CsvUserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USER_PREMIUM)
public class UserPremiumController {
    private final UserPremiumService userPremiumService;


    @GetMapping()
    public ResponseEntity<List<UserPremiumDto>> getListPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        if (userFilterDto == null) {
            log.error("userFilterDto ничего не содержит");
            throw new IllegalArgumentException("userFilterDto ничего не содержит");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userPremiumService.getPremiumUsers(userFilterDto));
    }


}
