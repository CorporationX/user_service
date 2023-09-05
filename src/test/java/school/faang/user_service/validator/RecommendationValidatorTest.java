package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRepository recommendationRepository;
    @InjectMocks
    private RecommendationValidator recommendationValidator;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private LocalDateTime recommendationCreatedAt;

    @BeforeEach
    void setUp() {
        recommendationCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        List<SkillOfferDto> skillOffers = Collections.singletonList(skillOfferDto);
        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("content")
                .skillOffers(skillOffers)
                .createdAt(recommendationCreatedAt)
                .build();

        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        recommendation = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("content")
                .skillOffers(Collections.emptyList())
                .createdAt(recommendationCreatedAt.minusMonths(7))
                .build();
        Skill skill = Skill.builder().id(1L).build();

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.ofNullable(recommendation));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
    }

    @Test
    void validateToCreate_shouldInvokeFindFirstByAuthorIdAndReceiverIdOrderByCreatedAtDescMethod() {
        recommendationValidator.validateToCreate(recommendationDto);
        verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L);
    }

    @Test
    void validateToCreate_shouldThrowDataValidationException_whenRecommendationDateIsBeforeSixMonthsLate() {
        recommendation.setCreatedAt(recommendationCreatedAt.minusMonths(5));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateToCreate(recommendationDto),
        "The author has already recommended this user in the last 6 months.");
    }

    @Test
    void validateToCreate_shouldThrowDataValidationException_whenSkillOfferDoesNotExist() {
        recommendationDto.getSkillOffers().get(0).setSkill(2L);

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateToCreate(recommendationDto),
                "One or more suggested skills do not exist in the system.");
    }

    @Test
    void validateToCreate_shouldNotThrowAnyException() {
        assertDoesNotThrow(() -> recommendationValidator.validateToCreate(recommendationDto));
    }

    @Test
    void validateToUpdate_shouldInvokeFindFirstByAuthorIdAndReceiverIdOrderByCreatedAtDescMethod() {
        recommendationValidator.validateToUpdate(recommendationDto);
        verify(recommendationRepository).findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L);
    }

    @Test
    void validateToUpdate_shouldThrowDataValidationException_whenRecommendationDateIsBeforeSixMonthsLate() {
        recommendation.setCreatedAt(recommendationCreatedAt.minusMonths(5));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateToUpdate(recommendationDto),
                "The author has already recommended this user in the last 6 months.");
    }

    @Test
    void validateToUpdate_shouldThrowDataValidationException_whenSkillOfferDoesNotExist() {
        recommendationDto.getSkillOffers().get(0).setSkill(2L);

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateToUpdate(recommendationDto),
                "One or more suggested skills do not exist in the system.");
    }

    @Test
    void validateToUpdate_shouldNotThrowAnyException() {
        assertDoesNotThrow(() -> recommendationValidator.validateToUpdate(recommendationDto));
    }
}