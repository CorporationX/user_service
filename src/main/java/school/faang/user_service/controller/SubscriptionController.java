package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping(value = "/follow")
    public void followUser(long followerId, long followeeId) {
        if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("ID пользователей должны быть положительными.");
        }
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на самого себя.");
        }
        subscriptionService.followUser(followerId, followeeId);
    }
}