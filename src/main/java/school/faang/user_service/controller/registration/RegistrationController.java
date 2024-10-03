package school.faang.user_service.controller.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserDtoForRegistration;
import school.faang.user_service.service.registration.RegistrationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/registration")
    public UserDto register(@Validated @RequestBody UserDtoForRegistration userDto) {
        return registrationService.register(userDto);
    }
}
