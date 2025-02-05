package school.faang.user_service.service.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.rating.UserRating;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class RatingAspect {

    private final RatingService ratingService;

    @After("@annotation(school.faang.user_service.service.rating.annotation.FollowUser)")
    public UserRating followUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.addScore(userId, "Subscription rating");
    }

    @After("@annotation(school.faang.user_service.service.rating.annotation.UnFollowUser)")
    public UserRating unfollowUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.minusScore(userId, "Subscription rating");
    }

    @After("@annotation(school.faang.user_service.service.rating.annotation.AcquireSkill)")
    public UserRating acquireSkillFromOffers(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[1];
        return ratingService.addScore(userId, "Skill rating");
    }

    @After("@annotation(school.faang.user_service.service.rating.annotation.CreateSkill)")
    public List<UserRating> createSkill(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Skill skill = (Skill) args[0];
        return skill.getUsers().stream()
                .map(user -> ratingService.addScore(user.getId(), "Skill rating"))
                .toList();
    }
}
