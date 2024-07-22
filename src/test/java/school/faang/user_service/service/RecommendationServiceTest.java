package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationValidation recommendationValidation;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecommendationService recommendationService;
    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;
    private User receiver;

    @BeforeEach
    void setUp(){
        author = new User();
        author.setId(1L);

        receiver = new User();
        receiver.setId(1L);

        Skill skill = new Skill();
        skill.setId(1L);

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setSkill(skill);

        recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setSkillOffers(List.of(skillOffer));

        SkillOfferDto skillOfferDto = new SkillOfferDto(1L, 1L, 1L);
        recommendationDto = new RecommendationDto(1L, 1L, 1L, "content", List.of(skillOfferDto), LocalDateTime.now());

        Skill skills = new Skill();
        skills.setId(1L);
    }
    @Test
    @DisplayName("Test to create a user with a recommendation.")
    void testCreate() {
        System.out.println(recommendation.getAuthor().getId());
        when(recommendationMapper.recommendationEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.recommendationDto(recommendation)).thenReturn(recommendationDto);
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);

        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationValidation, times(1)).recommendationIsNotEmpty(recommendationDto);
        verify(recommendationValidation, times(1)).validationHalfOfYear(recommendationDto);
        verify(recommendationValidation, times(1)).skillValid(recommendationDto.skillOffers());
        verify(recommendationMapper, times(1)).recommendationEntity(recommendationDto);
        verify(recommendationMapper, times(1)).recommendationDto(recommendation);
        verify(recommendationRepository, times(1)).save(recommendation);

        assertEquals(recommendationDto, result);
    }
    @Test
    void testUpdate() {
        when(recommendationMapper.recommendationEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.recommendationDto(recommendation)).thenReturn(recommendationDto);
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(recommendationValidation, times(1)).recommendationIsNotEmpty(recommendationDto);
        verify(recommendationValidation, times(1)).validationHalfOfYear(recommendationDto);
        verify(recommendationValidation, times(1)).skillValid(recommendationDto.skillOffers());
        verify(recommendationMapper, times(1)).recommendationEntity(recommendationDto);
        verify(recommendationMapper, times(1)).recommendationDto(recommendation);
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendation.getId());
        verify(skillOfferRepository, times(1)).save(any(SkillOffer.class));
        verify(recommendationRepository, times(1)).save(recommendation);

        assertEquals(recommendationDto, result);
    }

    @Test
    void testDelete() {
        Recommendation recommendationFound = new Recommendation();
        recommendationFound.setId(1L);

        when(recommendationRepository.findById(recommendationFound.getId()))
                .thenReturn(Optional.of(recommendationFound));
        recommendationService.delete(recommendationFound.getId());

        verify(recommendationRepository, times(1)).findById(recommendationFound.getId());
        verify(recommendationRepository, times(1)).deleteById(recommendationFound.getId());
        Optional<Recommendation> result = recommendationRepository.findById(recommendationFound.getId());
        assertTrue(result.isPresent());
    }

    @Test
    void testDeleteNotFound() {
        Recommendation recommendationNotFound = new Recommendation();
        recommendationRepository.findById(recommendationNotFound.getId());
        assertThrows(DataValidationException.class,
                () -> recommendationService.delete(recommendationNotFound.getId()));
    }
}