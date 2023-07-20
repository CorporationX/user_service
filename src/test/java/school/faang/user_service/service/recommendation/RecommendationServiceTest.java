package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @Test
    public void testCreate() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

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

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        assertDoesNotThrow(() -> recommendationService.create(recommendationDto));
        assertEquals(recommendationDto.getId(), result.getId());
        assertEquals(recommendationDto.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffers(), result.getSkillOffers());
    }

    @Test
    public void testCreate_InvalidSkillId_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L), new SkillOfferDto(2L, 2L)));

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    public void testCreate_RecommendationIntervalNotExceeded_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(List.of(new SkillOfferDto(1L, 1L)));

        Recommendation previousRecommendation = new Recommendation();
        previousRecommendation.setUpdatedAt(LocalDateTime.now().minusMonths(6 - 1));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(2L, 3L))
                .thenReturn(Optional.of(previousRecommendation));

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    public void testCreate_RecommendationEmptySkillsList_ThrowsDataValidationException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }
}
