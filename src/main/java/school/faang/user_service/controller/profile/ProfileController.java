package school.faang.user_service.controller.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.profile.ProfileService;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
@Tag(name = "Profiles")
@Slf4j
@Validated
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/view/{userId}")
    @Operation(summary = "Add profile view")
    public void addView(@PathVariable("userId") long userId) {
        profileService.addView(userId);
    }
}
