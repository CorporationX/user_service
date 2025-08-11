package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.SubscriptionService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/{followeeId}/followers-id")
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> getFollowersId(@PathVariable Long followeeId) {
        log.info("get user id followers. user: {}", followeeId);
        return subscriptionService.findFollowerIdsByFolloweeId(followeeId);
    }
}
