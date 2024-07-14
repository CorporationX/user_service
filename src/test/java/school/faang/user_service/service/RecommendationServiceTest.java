package school.faang.user_service.service;

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
import school.faang.user_service.exaception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.RecommendationValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    RecommendationServiceTest() {
    }

    @Test
    void testCreate() {
        List<SkillOfferDto> skillOfferDtos = List.of(new SkillOfferDto(1L, 1L, 1L));
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "content", skillOfferDtos, LocalDateTime.now());
        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(1L);
        recommendation.setReceiver(receiver);
        recommendation.setAuthor(author);

        Skill skill = new Skill();
        skill.setId(1L);

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setSkill(skill);
        recommendation.setSkillOffers(List.of(skillOffer));

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
        List<SkillOfferDto> skillOfferDtos = List.of(new SkillOfferDto(1L, 1L, 1L));
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "content", skillOfferDtos, LocalDateTime.now());
        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(1L);
        recommendation.setReceiver(receiver);
        recommendation.setAuthor(author);

        Skill skill = new Skill();
        skill.setId(1L);

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setSkill(skill);
        recommendation.setSkillOffers(List.of(skillOffer));

        when(recommendationMapper.recommendationEntity(recommendationDto)).thenReturn(recommendation);
        when(recommendationMapper.recommendationDto(recommendation)).thenReturn(recommendationDto);
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author), Optional.of(receiver));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));

        receiver.setSkills(List.of(skill));
        when(skillOfferRepository.save(skillOffer)).thenReturn(skillOffer);

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(recommendationValidation, times(1)).recommendationIsNotEmpty(recommendationDto);
        verify(recommendationValidation, times(1)).validationHalfOfYear(recommendationDto);
        verify(recommendationValidation, times(1)).skillValid(recommendationDto.skillOffers());
        verify(recommendationMapper, times(1)).recommendationEntity(recommendationDto);
        verify(recommendationMapper, times(1)).recommendationDto(recommendation);
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(recommendation.getId());
        verify(skillOfferRepository, times(1)).save(skillOffer);
        verify(recommendationRepository, times(1)).save(recommendation);

        assertEquals(recommendationDto, result);
    }

    @Test
    void testDelete() {

        Recommendation recommendationFound = new Recommendation();
        recommendationFound.setId(1L);

        assertEquals(1L, recommendationFound.getId());

        when(recommendationRepository.findById(recommendationFound.getId()))
                .thenReturn(Optional.of(recommendationFound));

        recommendationService.delete(recommendationFound.getId());

        verify(recommendationRepository, times(1)).findById(recommendationFound.getId());
        verify(recommendationRepository, times(1)).deleteById(recommendationFound.getId());
    }

    @Test
    void testDeleteNotFound() {

        Recommendation recommendationNotFound = new Recommendation();

        when(recommendationRepository.findById(recommendationNotFound.getId()))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class,
                () -> recommendationService.delete(recommendationNotFound.getId()));

    }
}