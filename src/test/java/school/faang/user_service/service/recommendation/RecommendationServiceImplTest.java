package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import school.faang.user_service.model.dto.recommendation.RecommendationDto;
import school.faang.user_service.model.dto.recommendation.SkillOfferDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.impl.recommendation.RecommendationServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationServiceValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {
    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationServiceValidator validator;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private long id;

    @BeforeEach
    void setup() {
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .skillOffers(List.of(new SkillOfferDto(2L, 1L), new SkillOfferDto(2L, 1L)))
                .authorId(5L)
                .receiverId(4L)
                .content("some content")
                .createdAt(LocalDateTime.now())
                .build();

        recommendation = Recommendation.builder()
                .id(1L)
                .author(new User())
                .receiver(new User())
                .content("some content")
                .createdAt(LocalDateTime.now())
                .build();

        id = 1L;
    }

    @Test
    void testCreateOk() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(id);

        assertEquals(recommendationDto, recommendationService.create(recommendationDto));

        verify(skillRepository, times(2)).findUserSkill(anyLong(), anyLong());
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());

    }

    @Test
    void testUpdateOk() {
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(id);

        assertEquals(recommendationDto, recommendationService.update(recommendationDto));

        verify(skillOfferRepository).deleteAllByRecommendationId(id);
        verify(skillOfferRepository, times(2)).create(anyLong(), anyLong());
    }

    @Test
    void testDeleteOk() {
        recommendationService.delete(id);

        verify(recommendationRepository).deleteById(id);
    }

    @Test
    void testGetAllUserRecommendations() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(anyLong(), Mockito.any())).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(Mockito.any())).thenReturn(recommendationDto);

        recommendationService.getAllUserRecommendations(id);

        verify(recommendationRepository).findAllByReceiverId(anyLong(), Mockito.any());
        verify(recommendationMapper).toDto(Mockito.any());
    }

    @Test
    void testGetAllGivenRecommendations() {
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(anyLong(), Mockito.any())).thenReturn(recommendationPage);
        when(recommendationMapper.toDto(Mockito.any())).thenReturn(recommendationDto);

        assertEquals(1, recommendationService.getAllGivenRecommendations(id).size());

        verify(recommendationRepository).findAllByAuthorId(anyLong(), Mockito.any());
        verify(recommendationMapper).toDto(Mockito.any());
    }
}
