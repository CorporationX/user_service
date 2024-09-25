package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.test_data.recommendation.TestDataRecommendation;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceHandlerTest {
    @InjectMocks
    private RecommendationServiceHandler recommendationServiceHandler;
    private User author;
    private User receiver;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        TestDataRecommendation testDataRecommendation = new TestDataRecommendation();

        author = testDataRecommendation.getAuthor();
        receiver = testDataRecommendation.getReceiver();
        recommendation = testDataRecommendation.getRecommendation();
    }

    @Test
    void testSelfRecommendationValidation_authorAndReceiverIsSameUser_throwsDataValidationException() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationServiceHandler.selfRecommendationValidation(author, author)
        );

        assertEquals("Cannot recommend yourself", exception.getMessage());
    }


    @Test
    void testRecommendationIntervalValidation_earlyRecommendationsGiven_throwsDataValidationException() {
        recommendation.setCreatedAt(LocalDateTime.now().minusHours(1));
        author.setRecommendationsGiven(List.of(recommendation));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationServiceHandler.recommendationIntervalValidation(author, receiver)
        );

        assertEquals("This receiver has already gave a recommendation less than 6 months to that author.",
                exception.getMessage()
        );
    }

    @Test
    void testSkillOffersValidation_skillDoesntExist_throwsDataValidationException() {
        List<Long> skillOfferDtoIds = List.of(1L, 2L, 3L);
        List<Long> allSkillsIds = List.of(1L, 2L);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationServiceHandler.skillOffersValidation(skillOfferDtoIds, allSkillsIds));

        assertEquals("SkillOffer of this recommendation not valid.", exception.getMessage());
    }
}
