package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.test_data.recommendation.TestDataRecommendation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationServiceHandler recommendationServiceHandler;
    @InjectMocks
    private RecommendationService recommendationService;

    private Skill skill1;
    private Skill skill2;
    private User author;
    private User receiver;
    private Recommendation recommendation;
    private RecommendationDto recommendationDto;
    private UserSkillGuarantee userSkillGuarantee;
    private int offset;
    private int limit;

    @BeforeEach
    void setUp() {
        TestDataRecommendation testDataRecommendation = new TestDataRecommendation();

        skill1 = testDataRecommendation.getSkill1();
        skill2 = testDataRecommendation.getSkill2();
        author = testDataRecommendation.getAuthor();
        receiver = testDataRecommendation.getReceiver();
        recommendation = testDataRecommendation.getRecommendation();
        recommendationDto = testDataRecommendation.getRecommendationDto();
        userSkillGuarantee = testDataRecommendation.getUserSkillGuarantee();
    }

    @Nested
    class PositiveTests {
        @Test
        void testCreateRecommendationSuccess() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.ofNullable(author));
            when(userRepository.findById(receiver.getId())).thenReturn(Optional.ofNullable(receiver));
            when(recommendationRepository.recommendationExistCheck(receiver.getId(), skill1.getId()))
                    .thenReturn(receiver.getSkills().size());

            RecommendationDto result = recommendationService.createRecommendation(recommendationDto);

            assertNotNull(result);
            assertEquals(author.getId(), result.getAuthorId());
            assertEquals(receiver.getId(), result.getReceiverId());

            verify(recommendationServiceHandler, atLeastOnce()).selfRecommendationValidation(author, receiver);
            verify(recommendationServiceHandler, atLeastOnce()).recommendationIntervalValidation(author, receiver);
            verify(skillRepository, atLeastOnce()).findAllById(anyList());
            verify(recommendationServiceHandler, atLeastOnce()).skillOffersValidation(anyList(), anyList());
            verify(userSkillGuaranteeRepository, atLeastOnce()).create(author.getId(), receiver.getId(), skill1.getId());
            verify(userSkillGuaranteeRepository, atLeastOnce()).findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(
                    author.getId(),
                    receiver.getId(),
                    skill1.getId()
            );
        }

        @Test
        void testUpdateRecommendationSuccess() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.ofNullable(author));
            when(userRepository.findById(receiver.getId())).thenReturn(Optional.ofNullable(receiver));
            when(recommendationRepository.recommendationExistCheck(receiver.getId(), skill1.getId()))
                    .thenReturn(receiver.getSkills().size());

            RecommendationDto result = recommendationService.updateRecommendation(recommendationDto);

            assertNotNull(result);
            assertEquals(author.getId(), result.getAuthorId());
            assertEquals(receiver.getId(), result.getReceiverId());

            verify(recommendationServiceHandler, atLeastOnce()).selfRecommendationValidation(author, receiver);
            verify(recommendationServiceHandler, atLeastOnce()).recommendationIntervalValidation(author, receiver);
            verify(skillRepository, atLeastOnce()).findAllById(anyList());
            verify(recommendationServiceHandler, atLeastOnce()).skillOffersValidation(anyList(), anyList());
            verify(userSkillGuaranteeRepository, atLeastOnce()).create(author.getId(), receiver.getId(), skill2.getId());
            verify(userSkillGuaranteeRepository, atLeastOnce()).findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(
                    author.getId(),
                    receiver.getId(),
                    skill2.getId()
            );
        }

        @Test
        void testDeleteRecommendationSuccess() {
            when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);

            recommendationService.deleteRecommendation(recommendation.getId());

            verify(recommendationRepository, atLeastOnce()).deleteById(recommendation.getId());
        }

        @Test
        void testGetAllUserRecommendationsSuccess() {
            offset = 0;
            limit = 10;
            List<Recommendation> recommendations = List.of(recommendation);
            PageImpl<Recommendation> recommendationPage = new PageImpl<>(recommendations);

            when(recommendationRepository.findAllByReceiverId(receiver.getId(), PageRequest.of(offset, limit)))
                    .thenReturn(recommendationPage);

            List<Recommendation> result = recommendationService.getAllUserRecommendations(receiver.getId(), offset, limit);
            assertNotNull(result);
            assertEquals(recommendations, result);

            verify(recommendationRepository, atLeastOnce()).findAllByReceiverId(receiver.getId(), PageRequest.of(offset, limit));
        }

        @Test
        void testGetAllGivenRecommendationsSuccess() {
            offset = 0;
            limit = 10;
            List<Recommendation> recommendations = List.of(recommendation);
            PageImpl<Recommendation> recommendationPage = new PageImpl<>(recommendations);

            when(recommendationRepository.findAllByAuthorId(author.getId(), PageRequest.of(offset, limit)))
                    .thenReturn(recommendationPage);

            List<Recommendation> result = recommendationService.getAllGivenRecommendations(author.getId(), offset, limit);
            assertNotNull(result);
            assertEquals(recommendations, result);

            verify(recommendationRepository, atLeastOnce()).findAllByAuthorId(author.getId(), PageRequest.of(offset, limit));
        }
    }

    @Nested
    class NegativeTest {
        @Test
        void testCreateRecommendation_guaranteeFromThisAuthorAlreadyExist_throwDataValidationException() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.ofNullable(author));
            when(userRepository.findById(receiver.getId())).thenReturn(Optional.ofNullable(receiver));
            when(recommendationRepository.recommendationExistCheck(receiver.getId(), skill1.getId()))
                    .thenReturn(receiver.getSkills().size());
            when(userSkillGuaranteeRepository.findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(
                    author.getId(),
                    receiver.getId(),
                    skill1.getId())
            ).thenReturn(Optional.of(userSkillGuarantee));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> recommendationService.createRecommendation(recommendationDto)
            );

            assertEquals(
                    "Guarantee for SkillOffer with ID: " + skill1.getId() +
                            " for User with ID: " + receiver.getId() +
                            " from Guarantor with ID: " + author.getId() +
                            " already exist",
                    exception.getMessage()
            );
        }

        @Test
        void testCreateRecommendation_userNotFound_throwDataValidationException() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> recommendationService.createRecommendation(recommendationDto)
            );

            assertEquals("User with ID: " + author.getId() + " not found", exception.getMessage());
        }

        @Test
        void testUpdateRecommendation_guaranteeFromThisAuthorAlreadyExist_throwDataValidationException() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.ofNullable(author));
            when(userRepository.findById(receiver.getId())).thenReturn(Optional.ofNullable(receiver));
            when(recommendationRepository.recommendationExistCheck(receiver.getId(), skill1.getId()))
                    .thenReturn(receiver.getSkills().size());
            when(userSkillGuaranteeRepository.findFirstByGuarantorIdAndUserIdAndSkillIdOrderById(
                    author.getId(),
                    receiver.getId(),
                    skill2.getId())
            ).thenReturn(Optional.of(userSkillGuarantee));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> recommendationService.updateRecommendation(recommendationDto)
            );

            assertEquals(
                    "Guarantee for SkillOffer with ID: " + skill2.getId() +
                            " for User with ID: " + receiver.getId() +
                            " from Guarantor with ID: " + author.getId() +
                            " already exist",
                    exception.getMessage()
            );
        }

        @Test
        void testUpdateRecommendation_userNotFound_throwDataValidationException() {
            when(userRepository.findById(author.getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> recommendationService.createRecommendation(recommendationDto)
            );

            assertEquals("User with ID: " + author.getId() + " not found", exception.getMessage());
        }

        @Test
        void testDeleteRecommendation_recommendationNotFound_throwDataValidationException() {
            when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> recommendationService.deleteRecommendation(recommendation.getId())
            );

            assertEquals("Recommendation with ID: " + recommendation.getId() + " not found", exception.getMessage());
        }


    }
}
