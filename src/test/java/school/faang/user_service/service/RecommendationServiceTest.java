package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.mapper.RecommendationMapper;
import school.faang.user_service.entity.mapper.SkillOfferMapper;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationMapper recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testCreateValidInputSuccess() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");
        recommendationEntity.setSkillOffers(List.of(
                new SkillOffer(1L, new Skill(), recommendationEntity),
                new SkillOffer(2L, new Skill(), recommendationEntity)));

        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertEquals(recommendationDto, result);
        verify(recommendationRepository, times(1)).save(recommendationEntity);
    }

    @Test
    public void testCreateRecommendationWithLastUpdate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);

        Recommendation recommendation = new Recommendation();
        recommendation.setUpdatedAt(LocalDateTime.now().minusMonths(4));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateRecommendationWithNullSkills() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(null);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateRecommendationWithEmptySkill() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateRecommendationWithExistIdOnSkillRepo() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testUpdateWithValidateException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);

        when(recommendationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationService.update(recommendationDto));
    }

    @Test
    public void testSuccessfulUpdate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(
                new SkillOfferDto(1L, 1L),
                new SkillOfferDto(2L, 2L)));

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");
        recommendationEntity.setSkillOffers(List.of(
                new SkillOffer(1L, new Skill(), recommendationEntity),
                new SkillOffer(2L, new Skill(), recommendationEntity)));

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.update(recommendationDto);

        verify(recommendationRepository, times(1)).deleteById(1L);
        verify(recommendationRepository, times(1)).save(recommendationEntity);

        assertEquals(result, recommendationDto);
    }

    @Test
    public void testDeleteById() {
        Long idToDelete = 1L;

        recommendationService.delete(idToDelete);

        verify(recommendationRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    public void testGetAllUserRecommendation() {

    }

    @Test
    public void testAllRecommendation() {

    }
}
