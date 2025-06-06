package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private RecommendationService recommendationService;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(
                recommendationService,
                "rangeBetweenRecommendation",
                6);
    }

    @Test
    void create_ShouldThrowException_IfRecentRecommendationExists() {
        // Arrange
        ReflectionTestUtils.setField(recommendationService,
                "rangeBetweenRecommendation", 6);

        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);

        Recommendation recent = new Recommendation();
        recent.setCreatedAt(LocalDateTime.now().minusMonths(1));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(recent));

        // Act + Assert
        assertThatThrownBy(() -> recommendationService.create(dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("You already gave a recommendation in the last 6 months.");
    }

    @Test
    void create_ShouldCreateRecommendation_IfNoRecentExists() {
        // Arrange
        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setContent("test");

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.empty());
        when(recommendationRepository.create(1L, 2L, "test")).thenReturn(42L);

        Recommendation saved = new Recommendation();
        saved.setId(42L);
        saved.setContent("test");
        saved.setCreatedAt(LocalDateTime.now());

        RecommendationDto mappedDto = new RecommendationDto();
        mappedDto.setId(42L);
        mappedDto.setContent("test");

        when(recommendationRepository.findById(42L)).thenReturn(Optional.of(saved));

        // Act
        RecommendationDto result = recommendationService.create(dto);

        // Assert
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getContent()).isEqualTo("test");

        verify(recommendationRepository).create(1L, 2L, "test");
        verify(recommendationRepository).findById(42L);
        verify(recommendationMapper).toDto(saved);
    }

    @Test
    void update_ShouldUpdateRecommendation_WhenExistingFound() {
        ReflectionTestUtils.setField(recommendationService,
                "rangeBetweenRecommendation", 6);

        RecommendationDto dto = new RecommendationDto();
        dto.setId(100L);
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setContent("updated");

        Recommendation existing = new Recommendation();
        existing.setId(100L);
        existing.setCreatedAt(LocalDateTime.now().minusMonths(7));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(existing));

        when(recommendationRepository.findById(100L))
                .thenReturn(Optional.of(existing));

        RecommendationDto mappedDto = new RecommendationDto();
        mappedDto.setId(100L);
        mappedDto.setContent("updated");
        doReturn(mappedDto).when(recommendationMapper).toDto(existing);

        RecommendationDto result = recommendationService.update(dto);

        verify(recommendationRepository).update(1L, 2L, "updated");
        verify(skillOfferRepository).deleteAllByRecommendationId(100L);
        verify(recommendationMapper).toDto(existing);

        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    void update_ShouldUpdateRecommendation_WhenNoExistingFound() {
        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setContent("updated");

        Recommendation existing = new Recommendation();

        existing.setCreatedAt(LocalDateTime.now().minusMonths(7));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationService.update(dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("No required recommendations found");
    }

    @Test
    void delete_ShouldDeleteRecommendation_WhenExistingFound() {
        Long id = 231L;
        when(recommendationRepository.existsById(id)).thenReturn(true);

        boolean deleted = recommendationService.delete(id);
        assertThat(deleted).isTrue();
        verify(recommendationRepository).deleteById(id);
    }

    @Test
    void delete_ShouldReturnFalse_WhenRecommendationDoesNotExist() {
        Long id = 231L;
        when(recommendationRepository.existsById(id)).thenReturn(false);
        boolean deleted = recommendationService.delete(id);

        assertThat(deleted).isFalse();
        verify(recommendationRepository, never()).deleteById(id);
    }

    @Test
    void getAllUserRecommendations_ShouldReturnMappedDto() {
        long receiverId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        RecommendationDto dto = new RecommendationDto();
        Recommendation recommendation = new Recommendation();

        ReflectionTestUtils.setField(recommendationService,
                "recommendationMapper", recommendationMapper);

        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(receiverId, pageable))
                .thenReturn(page);

        when(recommendationMapper.toDto(recommendation))
                .thenReturn(dto);

        Page<RecommendationDto> result =
                recommendationService.getAllUserRecommendations(receiverId, pageable);
        assertThat(result.getContent()).isEqualTo(List.of(dto));
        verify(recommendationRepository).findAllByReceiverId(receiverId, pageable);
    }

    @Test
    void getAllGivenRecommendations_ShouldReturnMappedDto() {
        long authorId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Recommendation recommendation = new Recommendation();
        RecommendationDto dto = new RecommendationDto();

        RecommendationMapper mapper = mock(RecommendationMapper.class);
        ReflectionTestUtils.setField(recommendationService, "recommendationMapper", mapper);

        Page<Recommendation> page = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(authorId, pageable))
                .thenReturn(page);
        when(mapper.toDto(recommendation)).thenReturn(dto);

        Page<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId, pageable);

        assertThat(result.getContent()).isEqualTo(List.of(dto));
        verify(recommendationRepository).findAllByAuthorId(authorId, pageable);
        verify(mapper).toDto(recommendation);
    }
}
