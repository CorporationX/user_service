package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static school.faang.user_service.exception.recommendation.RecommendationError.RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED;
import static school.faang.user_service.exception.recommendation.RecommendationError.SKILL_IS_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Spy
    RecommendationMapper recommendationMapper;
    @InjectMocks
    RecommendationService recommendationService;

    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    SkillOfferRepository skillOfferRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;
    private User receiver;
    private Skill skill;
    private SkillOffer skillOffer;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        receiver = new User();
        receiver.setId(2L);

        skill = new Skill();
        skill.setId(1L);

        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .recommendationId(1L)
                .skillId(1L)
                .build();

        recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(skillOfferDto))
                .build();

        recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setId(1L);

        skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        recommendation.setSkillOffers(List.of(skillOffer));
    }

    @Test
    void validationIntervalAndSkill_ShouldThrowDataValidationException_WhenIntervalNotPassed() {
        LocalDateTime recentDate = LocalDateTime.now().minusMonths(1);
        Recommendation recentRecommendation = new Recommendation();
        recentRecommendation.setCreatedAt(recentDate);
        recentRecommendation.setReceiver(receiver);
        author.setRecommendationsGiven(List.of(recentRecommendation));

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));


        DataValidationException exception = assertThrows(
                DataValidationException.class, () -> recommendationService.create(recommendationDto)
        );

        Assertions.assertEquals(RECOMMENDATION_EXPIRATION_TIME_NOT_PASSED.getMessage(), exception.getMessage());
    }

    @Test
    void validationIntervalAndSkill_ShouldThrowDataValidationException_WhenSkillNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillOfferRepository.existsById(anyLong())).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationService.create(recommendationDto));

        Assertions.assertEquals(SKILL_IS_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void create_ShouldReturnRecommendationDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(skillOfferRepository.existsById(anyLong())).thenReturn(true);

        when(recommendationMapper.toEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.toDto(recommendation)).thenReturn(recommendationDto);

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationDto result = recommendationService.create(recommendationDto);

        Assertions.assertEquals(recommendationDto, result);
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    }
}