package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SubscriptionDto;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PutMapping("/follow")
    public void followUser(@RequestBody SubscriptionDto subscriptionDto) {
        subscriptionService.followUser(subscriptionDto);
    }
}
