package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.impl.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(2L)
                .receiverId(3L)
                .message("message")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(8L)
                .requester(new User())
                .receiver(new User())
                .message("message 2")
                .build();
    }
  
    @Test
    public void testRecommendationRequestCreated() {
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setRecommendation(recommendation);
        Mockito.when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        Mockito.when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        Mockito.when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        recommendationRequestService.create(recommendationRequestDto);

        Mockito.verify(recommendationRequestValidator, Mockito.times(1)).validate(recommendationRequestDto);
        Mockito.verify(recommendationRequestMapper, Mockito.times(1)).toEntity(recommendationRequestDto);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(recommendationRequest);
        Mockito.verify(skillRequestRepository, Mockito.never()).create(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(recommendationRequestMapper, Mockito.times(1)).toEntity(recommendationRequestDto);
        Mockito.verify(recommendationEventPublisher, Mockito.times(1)).publish(Mockito.any(RecommendationEvent.class));
    }
}
