package school.faang.user_service.service.rating;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingAspectTest {
    @Mock
    RatingService ratingService;
    @InjectMocks
    RatingAspect ratingAspect;
    UserRating mockedResult;

    @Test
    void followUser() {
        setResultSubscriptionRating();
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(ratingService.addScore(3L, "Subscription rating")).thenReturn(mockedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{7L, 3L});

        UserRating actualResult = ratingAspect.followUser(joinPoint);

        Assertions.assertEquals(mockedResult, actualResult);
        verify(ratingService).addScore(3L, "Subscription rating");
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void unfollowUser() {
        setResultSubscriptionRating();
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(ratingService.minusScore(3L, "Subscription rating")).thenReturn(mockedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{7L, 3L});

        UserRating actualResult = ratingAspect.unfollowUser(joinPoint);

        Assertions.assertEquals(mockedResult, actualResult);
        verify(ratingService).minusScore(3L, "Subscription rating");
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void acquireSkillFromOffers() {
        setResultSkillRating();
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(ratingService.addScore(3L, "Skill rating")).thenReturn(mockedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{7L, 3L});

        UserRating actualResult = ratingAspect.acquireSkillFromOffers(joinPoint);

        Assertions.assertEquals(mockedResult, actualResult);
        verify(ratingService).addScore(3L, "Skill rating");
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void createSkill() {
        setResultSkillRating();
        UserRating mockedResultUser2 = UserRating.builder()
                .id(1L)
                .score(20)
                .type(UserRatingType.builder()
                        .id(2L)
                        .cost(20)
                        .name("Skill rating")
                        .isActivity(true)
                        .build())
                .user(User.builder()
                        .id(2L)
                        .username("testUser")
                        .build())
                .build();
        Skill skill = Skill.builder()
                .id(2L)
                .build();
        List<User> users = List.of(
                User.builder()
                        .id(3L)
                        .skills(List.of(skill))
                        .build(),
                User.builder()
                        .id(2L)
                        .skills(List.of(skill))
                        .build());
        skill.setUsers(users);

        JoinPoint joinPoint = mock(JoinPoint.class);

        when(ratingService.addScore(3L, "Skill rating")).thenReturn(mockedResult);
        when(ratingService.addScore(2L, "Skill rating")).thenReturn(mockedResultUser2);
        when(joinPoint.getArgs()).thenReturn(new Object[]{skill});

        List<UserRating> actualResult = ratingAspect.createSkill(joinPoint);
        List<UserRating> expected = List.of(mockedResult, mockedResultUser2);

        Assertions.assertEquals(expected, actualResult);
        verify(ratingService).addScore(3L, "Skill rating");
        verify(ratingService).addScore(2L, "Skill rating");
        verify(joinPoint, times(1)).getArgs();
    }

    void setResultSubscriptionRating() {
        mockedResult = UserRating.builder()
                .id(1L)
                .score(20)
                .type(UserRatingType.builder()
                        .id(2L)
                        .cost(20)
                        .name("Subscription rating")
                        .isActivity(true)
                        .build())
                .user(User.builder()
                        .id(3L)
                        .username("testUser")
                        .build())
                .build();
    }

    void setResultSkillRating() {
        mockedResult = UserRating.builder()
                .id(1L)
                .score(20)
                .type(UserRatingType.builder()
                        .id(2L)
                        .cost(20)
                        .name("Skill rating")
                        .isActivity(true)
                        .build())
                .user(User.builder()
                        .id(3L)
                        .username("testUser")
                        .build())
                .build();
    }
}