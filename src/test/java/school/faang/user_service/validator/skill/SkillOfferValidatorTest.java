package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class SkillOfferValidatorTest {
    private static final int MINIMUM_OFFERS = 3;
    private final long ANY_ID = 123L;
    private final String ANY_TITLE = "squat";
    private final String ANY_USERNAME = "username";
    @Mock
    private SkillOfferRepository skillOfferRepository;
    private Skill skill;
    private User user;
    private List<SkillOffer> skillOfferList;
    @InjectMocks
    private SkillOfferValidator skillOfferValidator;


    @BeforeEach
    public void init() {
        skill = Skill.builder()
                .id(ANY_ID)
                .title(ANY_TITLE)
                .build();

        user = User.builder()
                .id(ANY_ID)
                .username(ANY_USERNAME)
                .build();

        skillOfferList = List.of(new SkillOffer());
    }

    @Test
    @DisplayName("Exception when the skill recommended less then MINIMUM_OFFERS times")
    void whenFewRecommendationsThenThrowException() {
        assertThrows(DataValidationException.class,
                () -> skillOfferValidator.validateOffers(skillOfferList, skill, user), "The skill \"" + skill.getTitle() + "\" has been recommended to user " + user.getUsername() + " less than " + MINIMUM_OFFERS + " times");
    }
}