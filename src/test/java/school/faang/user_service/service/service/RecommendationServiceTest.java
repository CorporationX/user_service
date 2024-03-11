package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.recomendation.RecommendationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @InjectMocks
    RecommendationService recommendationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;
    @Mock
    private RecommendationRepository recommendationRepository;
    private RecommendationDto recommendationDto;
    @BeforeEach
    void setUp() {
        recommendationDto = RecommendationDto
                .builder()
                .id(1L)
                .authorId(2L)
                .receiverId(3L)
                .content("Message")
                .build();
    }

    @Test
    void testAuthorExistsIsInvalid() {
        when(userRepository.existsById(2L)).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("There are no author id in data base", illegalArgumentException.getMessage());
    }

    @Test
    void testReceiverExistsIsInvalid() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(false);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("There are no receiver id in data base", illegalArgumentException.getMessage());
    }

    @Test
    void testRecommendationToYourselfIsInvalid() {
        when(userRepository.existsById(2L)).thenReturn(true);
        recommendationDto.setReceiverId(2L);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> recommendationService.create(recommendationDto));
        assertEquals("You can not write a recommendation to yourself", illegalArgumentException.getMessage());
    }

    @Test
    void testToEntityIsSuccessful() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        recommendationService.create(recommendationDto);
        verify(recommendationMapper, times(1)).toEntity(recommendationDto);
    }

    @Test
    void testPublishIsSuccessful() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        recommendationService.create(recommendationDto);
        verify(recommendationEventPublisher, times(1)).publish(any(RecommendationEvent.class));
    }

}