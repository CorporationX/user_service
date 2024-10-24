package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.dto.event.RecommendationReceivedEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.impl.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long RECOMMENDATION_ID = 1L;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationReceivedEventPublisher eventPublisher;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private Skill skill;
    private User author;
    private User receiver;

    @BeforeEach
    void setUp() {
        author = User.builder().id(AUTHOR_ID).build();
        receiver = User.builder().id(RECEIVER_ID).build();
        skill = Skill.builder().id(SKILL_ID).build();

        recommendation = Recommendation.builder()
                .id(RECOMMENDATION_ID)
                .author(author)
                .receiver(receiver)
                .content("Recommendation content")
                .skillOffers(List.of())
                .createdAt(LocalDateTime.now())
                .build();

        recommendationDto = RecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content("Recommendation content")
                .skillOffers(List.of(new SkillOfferDto(SKILL_ID, RECOMMENDATION_ID, SKILL_ID)))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Update recommendation: recommendation not found")
    void updateRecommendation_ShouldThrowExceptionWhenRecommendationNotFound() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationService.updateRecommendation(RECOMMENDATION_ID, recommendationDto));

        assertEquals("Recommendation with id " + RECOMMENDATION_ID + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Get all recommendations for the recipient with a non-empty response")
    void getAllUserRecommendations_ShouldReturnRecommendationsWhenValid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID, pageable);

        assertEquals(1, result.size());
        assertEquals(recommendationDto, result.get(0));
        verify(recommendationRepository).findAllByReceiverId(RECEIVER_ID, pageable);
    }

    @Test
    @DisplayName("Get all recommendations from the author with a non-empty response")
    void getAllGivenRecommendations_ShouldReturnRecommendationsWhenValid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID, pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(recommendationDto, result.get(0));
        verify(recommendationRepository).findAllByAuthorId(AUTHOR_ID, pageable);
    }

    @Test
    @DisplayName("Update recommendation: successful update")
    void updateRecommendation_ShouldUpdateSuccessfully() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));
        when(skillRepository.findAllById(List.of(SKILL_ID))).thenReturn(List.of(skill));

        doAnswer(invocation -> {
            RecommendationDto dto = invocation.getArgument(0);
            recommendation.setContent(dto.content());
            recommendation.setReceiver(User.builder().id(dto.receiverId()).build());
            recommendation.setAuthor(User.builder().id(dto.authorId()).build());
            return null;
        }).when(recommendationMapper).updateFromDto(recommendationDto, recommendation);

        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.updateRecommendation(RECOMMENDATION_ID, recommendationDto);

        assertEquals(recommendationDto, result);
        assertEquals(recommendationDto.content(), recommendation.getContent());
        assertEquals(recommendationDto.authorId(), recommendation.getAuthor().getId());
        assertEquals(recommendationDto.receiverId(), recommendation.getReceiver().getId());

        verify(recommendationRepository).save(recommendation);
        verify(skillOfferRepository).deleteAllByRecommendationId(RECOMMENDATION_ID);
    }

    @Test
    @DisplayName("Delete recommendation with correct ID")
    void deleteRecommendation_ShouldDeleteSuccessfully() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));

        recommendationService.deleteRecommendation(RECOMMENDATION_ID);

        verify(recommendationRepository).delete(recommendation);
    }

    @Test
    @DisplayName("Delete recommendation: recommendation not found")
    void deleteRecommendation_ShouldThrowExceptionWhenRecommendationNotFound() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationService.deleteRecommendation(RECOMMENDATION_ID));

        assertEquals("Recommendation with id " + RECOMMENDATION_ID + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Create recommendation: event is published")
    void testCreateRecommendation_PublishesEvent() {
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content("Great work!")
                .skillOffers(List.of(new SkillOfferDto(SKILL_ID, RECOMMENDATION_ID, SKILL_ID)))
                .createdAt(LocalDateTime.now())
                .build();

        Recommendation recommendation = Recommendation.builder()
                .id(RECOMMENDATION_ID)
                .author(author)
                .receiver(receiver)
                .content("Great work!")
                .skillOffers(List.of())
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(AUTHOR_ID)).thenReturn(Optional.of(author));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiver));
        when(recommendationMapper.toRecommendation(recommendationDto)).thenReturn(recommendation);
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        when(skillRepository.findAllById(anyIterable())).thenReturn(List.of(skill));

        RecommendationDto result = recommendationService.createRecommendation(recommendationDto);

        assertNotNull(result);
        assertEquals(recommendationDto, result);
        verify(eventPublisher, times(1)).publish(any(RecommendationReceivedEvent.class));
    }
}