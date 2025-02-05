package school.faang.user_service.service.rating.user_rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionRankTest {
    @Mock
    UserService userService;
    @Mock
    RatingTypeService ratingTypeService;
    @Mock
    SubscriptionService subscriptionService;
    @InjectMocks
    SubscriptionRank subscriptionRank;

    @Test
    void isApplicable() {
        UserRatingDto userRatingDto = UserRatingDto.builder()
                .followeeRating(true)
                .build();
        assertTrue(subscriptionRank.isApplicable(userRatingDto));
    }

    @Test
    void isApplicableFalse() {
        UserRatingDto userRatingDto = UserRatingDto.builder()
                .followeeRating(false)
                .build();
        assertFalse(subscriptionRank.isApplicable(userRatingDto));
    }

    @Test
    void isApplicableIsNull() {
        assertFalse(subscriptionRank.isApplicable(null));
    }

    @Test
    void calculate() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        UserRatingDto ratings = UserRatingDto.builder()
                .followeeRating(true)
                .build();

        UserRatingType ratingType = UserRatingType.builder()
                .name("Subscription rating")
                .cost(10)
                .build();

        when(ratingTypeService.findByName("Subscription rating")).thenReturn(ratingType);

        when(subscriptionService.getFollowersCount(1L)).thenReturn(1);
        when(subscriptionService.getFollowersCount(2L)).thenReturn(2);

        when(userService.getUser(1L)).thenReturn(User.builder().id(1L).build());
        when(userService.getUser(2L)).thenReturn(User.builder().id(2L).build());

        List<UserRating> actualRatings = subscriptionRank.calculate(userIds, ratings);

        assertEquals(2, actualRatings.size());
        assertEquals(1, actualRatings.get(0).getUser().getId());
        assertEquals(2, actualRatings.get(1).getUser().getId());
        assertEquals(10, actualRatings.get(0).getScore());
        assertEquals(20, actualRatings.get(1).getScore());
        assertEquals("Subscription rating", actualRatings.get(0).getType().getName());
        assertEquals("Subscription rating", actualRatings.get(1).getType().getName());

        verify(ratingTypeService, times(1)).findByName("Subscription rating");
        verify(subscriptionService, times(2)).getFollowersCount(anyLong());
        verify(userService, times(2)).getUser(anyLong());
    }

    @Test
    void calculateUserIdsIsNull() {
        assertThrows(DataValidationException.class, () -> subscriptionRank.calculate(null,
                UserRatingDto.builder()
                        .build()));
    }

    @Test
    void calculateUserIdsIsEmpty() {
        assertThrows(DataValidationException.class, () -> subscriptionRank.calculate(List.of(),
                UserRatingDto.builder()
                        .build()));
    }

    @Test
    void calculateRatingsIsNull() {
        assertThrows(DataValidationException.class, () -> subscriptionRank.calculate(List.of(1L, 2L), null));
    }
}