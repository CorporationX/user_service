package school.faang.user_service.service.recommendation_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.dto.recomendation.RejectRecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recomendation.request.RecommendationRequestRejectException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recomendation.RecommendationRequestMapper;
import school.faang.user_service.service.recomendation.RecommendationRequestService;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestIdFilter;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestMessageFilter;
import school.faang.user_service.service.recomendation.filters.RecommendationRequestStatusFilter;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void testCreateSuccess() {
        RecommendationRequest createdRequest = new RecommendationRequest();
        createdRequest.setId(1L);

        CreateRecommendationRequestDto dto = this.validCreatedDto();
        User receiver = new User();
        User requester = new User();
        receiver.setId(dto.getReceiverId());
        requester.setId(dto.getRequesterId());

        when(this.recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(createdRequest);

        Long result = this.recommendationRequestService.create(
            this.mapper.toEntity(dto), dto.getSkills()
        );

        assertEquals(result, createdRequest.getId());

        RecommendationRequest recommendationEntity = new RecommendationRequest();
        recommendationEntity.setMessage(dto.getMessage());
        recommendationEntity.setStatus(RequestStatus.PENDING);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setRequester(requester);

        verify(this.recommendationRequestRepository, times(1)).save(recommendationEntity);
    }

    @Test
    public void testCreateValidationError() {
        CreateRecommendationRequestDto dto = this.validCreatedDto();

        doThrow(ValidationException.class).when(this.validator).validateCreateRecommendationRequest(
            any(RecommendationRequest.class), anyList()
        );

        assertThrows(ValidationException.class, () -> this.recommendationRequestService.create(
            this.mapper.toEntity(dto), dto.getSkills()
        ));

        verify(this.recommendationRequestRepository, times(0)).save(any(RecommendationRequest.class));
    }

    @Test
    public void testSuccessfulReject() {
        RejectRecommendationRequestDto rejectionDto = new RejectRecommendationRequestDto();
        rejectionDto.setId(1L);
        rejectionDto.setReason("Reject");

        RecommendationRequest recommendationRequest = this.mapper.toEntity(this.validCreatedDto());
        recommendationRequest.setId(rejectionDto.getId());
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(this.recommendationRequestRepository.findById(anyLong()))
            .thenReturn(Optional.of(recommendationRequest));

        this.recommendationRequestService.rejectRequest(rejectionDto);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        verify(this.recommendationRequestRepository, times(1)).save(recommendationRequest);
    }

    @Test
    public void testFailedReject() {
        RejectRecommendationRequestDto rejectionDto = new RejectRecommendationRequestDto();
        rejectionDto.setId(1L);
        rejectionDto.setReason("Reject");

        assertThrows(RecommendationRequestNotFoundException.class, () -> this.recommendationRequestService
            .rejectRequest(rejectionDto)
        );

        RecommendationRequest recommendationRequest = this.mapper.toEntity(this.validCreatedDto());
        recommendationRequest.setId(rejectionDto.getId());
        recommendationRequest.setStatus(RequestStatus.REJECTED);

        when(this.recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(recommendationRequest));
        assertThrows(RecommendationRequestRejectException.class, () -> this.recommendationRequestService
                .rejectRequest(rejectionDto)
        );
    }

    @Test
    public void testFailedGetOneRequest() {
        Long findId = 1L;

        assertThrows(RecommendationRequestNotFoundException.class, () -> this.recommendationRequestService
                .findRequestById(findId)
        );
    }

    @Test
    public void testSuccessGetOneRequest() {
        Long findId = 1L;
        RecommendationRequest recommendationRequest = this.mapper.toEntity(this.validCreatedDto());
        recommendationRequest.setId(findId);

        when(this.recommendationRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = this.recommendationRequestService.findRequestById(findId);

        assertEquals(result.getId(), findId);
        verify(this.recommendationRequestRepository, times(1)).findById(findId);
    }

    @Test
    public void testSearchRequests() {
        FilterRecommendationRequestsDto filterRecommendationRequestsDto = new FilterRecommendationRequestsDto();
        filterRecommendationRequestsDto.setStatus(RequestStatus.PENDING);

        this.recommendationRequestService = new RecommendationRequestService(
                List.of(
                    new RecommendationRequestIdFilter(),
                    new RecommendationRequestStatusFilter(),
                    new RecommendationRequestMessageFilter()
                ),
                this.skillRequestRepository,
                this.recommendationRequestRepository,
                this.validator
        );

        when(this.recommendationRequestRepository.findAll())
            .thenReturn(List.of(
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
            ));

        List<RecommendationRequest> result1 = this.recommendationRequestService.getRecommendationRequests(
            filterRecommendationRequestsDto
        );

        List<RecommendationRequest> result2 = this.recommendationRequestService.getRecommendationRequests(
                new FilterRecommendationRequestsDto()
        );

        assertEquals(result1.size(), 1);
        assertEquals(result2.size(), 2);
    }

    private CreateRecommendationRequestDto validCreatedDto() {
        return new CreateRecommendationRequestDto (
            "Message",
            new ArrayList<>(List.of(1L, 2L)),
            1L,
            2L
        );
    }
}
