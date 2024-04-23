package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.SkillAcquiredEvent;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationEventPublisher;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.impl.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private RejectionDto rejectionDto;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

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

        rejectionDto = RejectionDto.builder()
                .reason("message")
                .build();
    }

    @Test
    public void testRecommendationRequestCreated(){
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
        Mockito.verify(recommendationEventPublisher, Mockito.times(1)).publish(Mockito.any(RecommendationEvent.class));
        Mockito.verify(recommendationRequestMapper, Mockito.times(1)).toDto(recommendationRequest);
    }

    @Test
    public void testVerifySkillAcquiredPublishSuccess(){
        User author = new User();
        author.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());

        List<Long> skillIds = new ArrayList<>(List.of(1L));
        recommendationRequestDto.setSkillIds(skillIds);
        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setId(1L);
        List<SkillRequest> skillRequests = List.of(skillRequest1);
        recommendationRequest.setRecommendation(recommendation);
        recommendationRequest.setSkills(skillRequests);

        Mockito.when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        Mockito.when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        Mockito.when(skillRequestRepository.create(8L, 1L)).thenReturn(skillRequest1);
        Mockito.when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        recommendationRequestService.create(recommendationRequestDto);

        Mockito.verify(recommendationRequestValidator, Mockito.times(1)).validate(recommendationRequestDto);
        Mockito.verify(recommendationRequestMapper, Mockito.times(1)).toEntity(recommendationRequestDto);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(recommendationRequest);
        Mockito.verify(recommendationEventPublisher, Mockito.times(1)).publish(Mockito.any(RecommendationEvent.class));
        Mockito.verify(skillRequestRepository, Mockito.times(1)).create(8L, 1L);
        Mockito.verify(skillAcquiredEventPublisher, Mockito.times(1)).publish(Mockito.any(SkillAcquiredEvent.class));
        Mockito.verify(recommendationRequestMapper, Mockito.times(1)).toDto(recommendationRequest);
    }

//    @Test
//    public void testRecommendationRequestCreated() {
//        recommendationRequestService.create(recommendationRequestDto);
//        Mockito.verify(recommendationRequestService).create(recommendationRequestDto);
//        Mockito.when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);
//    }

//    @Test
//    public void testRecommendationRequestFindOne() {
//        long validId = 8;
//        Mockito.when(recommendationRequestRepository.findById(validId)).thenReturn(Optional.of(recommendationRequest));
//        recommendationRequestService.getRequest(validId);
//        Mockito.verify(recommendationRequestService).getRequest(validId);
//    }

//    @Test
//    public void testRecommendationRequestsFindAll() {
//        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
//    }

//    @Test
//    public void testRecommendationRequestReject() {
//        long id = 8;
//
//        recommendationRequestService.rejectRequest(id, rejectionDto);
//        Mockito.verify(recommendationRequestService).rejectRequest(id, rejectionDto);
//    }

//    @Test
//    public void testGetRequestThrowEntityNotFound() {
//        long requestId = 1L;
//
//        Mockito.when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());
//        Mockito.when(recommendationRequestService.getRequest(requestId)).thenThrow(EntityNotFoundException.class);
//    }
}
