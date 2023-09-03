package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import java.util.List;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequestDTO;
    private RecommendationRequest recommendationRequest;
    private RejectionDto rejection;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
     
    @BeforeEach
    void setUp() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(1);

        recommendationRequest = RecommendationRequest.builder()
                .id(55L)
                .message("message")
                .skills(List.of(skillRequest))
                .status(RequestStatus.PENDING)
                .build();

        recommendationRequestDTO = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(List.of(skillRequest))
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testRecommendationRequestCreated () {
        Mockito.when(recommendationRequestRepository.create(4L,11L, "message"))
                .thenReturn(recommendationRequest);
        recommendationRequestService.create(recommendationRequestDTO);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1))
                .create(4L,11L, "message");
    }

    @Test
    public void testRequestNotFound() {
        long invalidId = 1236;
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestService.getRequest(invalidId)
        );
    }

    @Test
    public void testRequestFound() {
        long validId = 55;
        Mockito.when(recommendationRequestRepository.findById(55L)).thenReturn(Optional.of(recommendationRequest));
        recommendationRequestService.getRequest(validId);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).findById(validId);
    }

    @Test
    public void testRequestForRejectionNotFound() {
        long id = 0;
        rejection = RejectionDto.builder().reason("reason").build();
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestService.rejectRequest(id, rejection)
        );
    }

    @Test
    public void testRequestRejected() {
        long id = 123;
        rejection = RejectionDto.builder().reason("reason").build();
        Mockito.when(recommendationRequestRepository.findById(123L)).thenReturn(Optional.of(recommendationRequest));
        recommendationRequestService.rejectRequest(id, rejection);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(recommendationRequest);
    }
}