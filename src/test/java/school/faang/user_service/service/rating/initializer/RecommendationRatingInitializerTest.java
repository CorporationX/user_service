package school.faang.user_service.service.rating.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.user.UserService;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRatingInitializerTest {

    private static final RatingType RATING_TYPE = RatingType.RECOMMENDATION_RATING;
    private static final Double RATING_COST = 10.0;

    private static final Long USER_ID = 1L;

    private static final Integer GIVEN_RECOMMENDATIONS = 2;

    private static final Integer RECEIVED_RECOMMENDATIONS = 3;

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private UserService userService;

    @Mock
    private UserRatingTypeService userRatingTypeService;

    @InjectMocks
    private RecommendationRatingInitializer recommendationRatingInitializer;

    @BeforeEach
    void setUp() {
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setName(RATING_TYPE);
        userRatingType.setCost(RATING_COST);

        when(userRatingTypeService.findByName(RATING_TYPE)).thenReturn(userRatingType);
        when(recommendationService.getNumberOfGivenRecommendationsPerUser()).thenReturn(Map.of(USER_ID, 2));
        when(recommendationService.getNumberOfReceivedRecommendationsPerUser()).thenReturn(Map.of(USER_ID, 3));
    }

    @Test
    void initializeRating() {
        recommendationRatingInitializer.initializeRating();

        verify(userService, times(1)).addUserRatingScore(USER_ID, GIVEN_RECOMMENDATIONS * RATING_COST);
        verify(userService, times(1)).addUserRatingScore(USER_ID, RECEIVED_RECOMMENDATIONS * RATING_COST);
    }
}