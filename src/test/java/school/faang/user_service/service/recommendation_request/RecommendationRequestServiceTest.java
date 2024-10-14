package school.faang.user_service.service.recommendation_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.dto.recomendation.RejectRecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestRejectException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.service.recomendation.RecommendationRequestService;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestIdFilter;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestMessageFilter;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestStatusFilter;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);
    @Mock
    private RecommendationRequestValidator validator;

    private CreateRecommendationRequestDto validRequestDto;
    private List<RecommendationRequest> recommendationRequests;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequestDto = new CreateRecommendationRequestDto(
                "Message",
                new ArrayList<>(List.of(1L, 2L)),
                1L,
                2L
        );

        recommendationRequests = List.of(
                new RecommendationRequest().builder()
                        .id(1L)
                        .message("Request 1")
                        .status(RequestStatus.PENDING)
                        .build(),
                new RecommendationRequest().builder()
                        .id(2L)
                        .message("Request 2")
                        .status(RequestStatus.ACCEPTED)
                        .build()
        );
    }

    @Test
    public void testCreateSuccess() {
        CreateRecommendationRequestDto dto = validRequestDto;
        User receiver = new User();
        User requester = new User();
        receiver.setId(dto.getReceiverId());
        requester.setId(dto.getRequesterId());

        RecommendationRequest recommendationEntity = new RecommendationRequest();
        recommendationEntity.setMessage(dto.getMessage());
        recommendationEntity.setStatus(RequestStatus.PENDING);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setRequester(requester);

        RecommendationRequest createdRequest = new RecommendationRequest();
        createdRequest.setId(1L);

        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(createdRequest);

        RecommendationRequest result = recommendationRequestService.create(
                mapper.toEntity(dto), dto.getSkills()
        );

        assertEquals(result.getId(), createdRequest.getId());
        verify(recommendationRequestRepository).save(recommendationEntity);
    }

    @Test
    public void testSuccessfulReject() {
        RejectRecommendationRequestDto rejectionDto = new RejectRecommendationRequestDto(1L, "Reject");

        RecommendationRequest findedRecommendationRequest = mapper.toEntity(validRequestDto);
        findedRecommendationRequest.setId(rejectionDto.getId());
        findedRecommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRecommendationRequest = mapper.toEntity(validRequestDto);
        savedRecommendationRequest.setId(rejectionDto.getId());
        savedRecommendationRequest.setStatus(RequestStatus.REJECTED);
        savedRecommendationRequest.setRejectionReason(rejectionDto.getReason());

        when(recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(findedRecommendationRequest));

        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(savedRecommendationRequest);

        RecommendationRequest result = recommendationRequestService.rejectRequest(findedRecommendationRequest);

        assertEquals(result.getStatus(), RequestStatus.REJECTED);
        assertEquals(result.getRejectionReason(), rejectionDto.getReason());
    }

    @Test
    public void testFailedRejectNotFound() {
        RejectRecommendationRequestDto rejectionDto = new RejectRecommendationRequestDto(1L, "Reject");

        RecommendationRequest recommendationRequest = mapper.toRejectEntity(rejectionDto);

        assertThrows(RecommendationRequestNotFoundException.class, () -> recommendationRequestService
                .rejectRequest(recommendationRequest)
        );
    }

    @Test
    public void testFailedRejectStatus() {
        RejectRecommendationRequestDto rejectionDto = new RejectRecommendationRequestDto(1L, "Reject");

        RecommendationRequest findedRecommendationRequest = mapper.toRejectEntity(rejectionDto);
        findedRecommendationRequest.setId(rejectionDto.getId());
        findedRecommendationRequest.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(findedRecommendationRequest));

        assertThrows(RecommendationRequestRejectException.class, () -> recommendationRequestService
                .rejectRequest(mapper.toRejectEntity(rejectionDto))
        );
    }

    @Test
    public void testFailedGetOneRequest() {
        Long findId = 1L;

        assertThrows(RecommendationRequestNotFoundException.class, () -> recommendationRequestService
                .findRequestById(findId)
        );
    }

    @Test
    public void testSuccessGetOneRequest() {
        Long findId = 1L;
        RecommendationRequest recommendationRequest = mapper.toEntity(validRequestDto);
        recommendationRequest.setId(findId);

        when(recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = recommendationRequestService.findRequestById(findId);

        assertEquals(result.getId(), findId);
        verify(recommendationRequestRepository).findById(findId);
    }

    @Test
    public void testSearchRequests() {
        FilterRecommendationRequestsDto filterRecommendationRequestsDto = new FilterRecommendationRequestsDto();
        filterRecommendationRequestsDto.setStatus(RequestStatus.PENDING);

        recommendationRequestService = new RecommendationRequestService(
                List.of(
                        new RecommendationRequestIdFilter(),
                        new RecommendationRequestStatusFilter(),
                        new RecommendationRequestMessageFilter()
                ),
                skillRequestRepository,
                recommendationRequestRepository,
                validator
        );

        when(recommendationRequestRepository.findAll())
                .thenReturn(recommendationRequests);

        List<RecommendationRequest> result1 = recommendationRequestService.getRecommendationRequests(
                filterRecommendationRequestsDto
        );

        List<RecommendationRequest> result2 = recommendationRequestService.getRecommendationRequests(
                new FilterRecommendationRequestsDto()
        );

        assertEquals(result1.size(), 1);
        assertEquals(result2.size(), 2);
    }
}
