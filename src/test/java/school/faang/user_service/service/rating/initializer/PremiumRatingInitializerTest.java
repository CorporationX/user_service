package school.faang.user_service.service.rating.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.rating.UserRatingTypeService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumRatingInitializerTest {

    private static final RatingType RATING_TYPE = RatingType.PREMIUM_RATING;

    private static final Double RATING_COST = 10.0;

    private static final List<Long> PREMIUM_USERS = List.of(1L, 2L);

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserService userService;

    @Mock
    private UserRatingTypeService userRatingTypeService;

    @InjectMocks
    private PremiumRatingInitializer premiumRatingInitializer;

    @BeforeEach
    void setUp() {
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setName(RATING_TYPE);
        userRatingType.setCost(RATING_COST);

        when(userRatingTypeService.findByName(RATING_TYPE)).thenReturn(userRatingType);
        when(premiumService.getPremiumUsers()).thenReturn(PREMIUM_USERS);
    }

    @Test
    void initializeRating() {
        premiumRatingInitializer.initializeRating();

        PREMIUM_USERS.forEach(userId -> {
            verify(userService, times(1)).addUserRatingScore(userId, RATING_COST);
        });
    }
}