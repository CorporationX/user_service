package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserContext userContext;

    @GetMapping("/followers/count")
    public int getFollowersCount() {
        long followerId = userContext.getUserId();
        return subscriptionService.getFollowersCount(followerId);
    }
}
