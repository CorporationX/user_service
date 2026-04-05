package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserSubscriptionService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/fanng")
public class UserSubscriptionController {
    private final UserSubscriptionService userSubscriptionService;

    @PatchMapping("/subscription")
    public void followUser(@Min(1) long followeeId) {
        userSubscriptionService.followUser(followeeId);
    }
}