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
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillRatingInitializerTest {

    private static final RatingType RATING_TYPE = RatingType.SKILL_RATING;
    private static final Double RATING_COST = 5.0;
    private static final Long USER_ID = 1L;
    private static final Integer NUM_OF_SKILLS = 4;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @Mock
    private UserRatingTypeService userRatingTypeService;

    @InjectMocks
    private SkillRatingInitializer skillRatingInitializer;

    @BeforeEach
    void setUp() {
        UserRatingType userRatingType = new UserRatingType();
        userRatingType.setName(RATING_TYPE);
        userRatingType.setCost(RATING_COST);

        when(userRatingTypeService.findByName(RATING_TYPE)).thenReturn(userRatingType);
        when(skillService.getNumberOfSkillsPerUser()).thenReturn(Map.of(USER_ID, NUM_OF_SKILLS));
    }

    @Test
    void initializeRating() {
        skillRatingInitializer.initializeRating();

        verify(userService, times(1)).addUserRatingScore(USER_ID, NUM_OF_SKILLS * RATING_COST);
    }
}