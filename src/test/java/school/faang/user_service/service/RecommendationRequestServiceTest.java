package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import java.util.List;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequest;
    private RecommendationRequest requestData;
    private RejectionDto rejection;
    private UserRepository userRepository;   
        
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService; 
    
     
    @BeforeEach
    void setUp() {
        recommendationRequest = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(null)
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testRequesterNotExistValidation() {
        recommendationRequest.setRequesterId(8L);
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateUsersExist(recommendationRequest)
        );
    }

    @Test
    public void testReceiverNotExistValidation() {
        recommendationRequest.setReceiverId(8888888L);
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateUsersExist(recommendationRequest)
        );
    }

    @Test
    public void testUserExistsValidation() {
        recommendationRequest.setRequesterId(1L);
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateUsersExist(recommendationRequest));
    }

    @Test
    public void testSkillNotExistValidation() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateSkillsExist(recommendationRequest)
        );
    }

    @Test
    public void testSkillExistsValidation() {
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateSkillsExist(recommendationRequest));
    }

    @Test
    public void testRequestPeriodValidation() {
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestValidator.validateRequestPeriod(recommendationRequest)
        );
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
        recommendationRequestService.rejectRequest(id, rejection);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(requestData);
    }

    @Test
    public void testRecommendationRequestStatusFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest3.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().status(RequestStatus.REJECTED).build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        Assertions.assertEquals(2, requestsByFilter.size());
        Assertions.assertEquals(RequestStatus.REJECTED, requestsByFilter.get(0).getStatus());
        Assertions.assertEquals(RequestStatus.REJECTED, requestsByFilter.get(1).getStatus());
    }

    @Test
    public void testRecommendationRequestRequesterIdFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setRequester(User.builder().id(1L).build());
        recommendationRequest2.setRequester(User.builder().id(2L).build());
        recommendationRequest3.setRequester(User.builder().id(1L).build());

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().receiverId(1L).build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        Assertions.assertEquals(2, requestsByFilter.size());
        Assertions.assertEquals(1L, requestsByFilter.get(0).getReceiverId());
        Assertions.assertEquals(1L, requestsByFilter.get(1).getReceiverId());
    }

    @Test
    public void testGetRequestsWithManyFilters() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest1.setRequester(User.builder().id(1L).build());
        recommendationRequest1.setReceiver(User.builder().id(2L).build());
        recommendationRequest1.setCreatedAt(LocalDateTime.now().minusMonths(2));

        recommendationRequest2.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setRequester(User.builder().id(2L).build());
        recommendationRequest2.setReceiver(User.builder().id(1L).build());
        recommendationRequest2.setCreatedAt(LocalDateTime.now().minusMonths(1));

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now().minusMonths(2))
                .build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        List<RecommendationRequestDto> expectedResult = requests.stream().map(recommendationRequestMapper::toDto).toList();

        Assertions.assertEquals(expectedResult, requestsByFilter);
    }
}