package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation.RequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.recommendation.SkillRequestValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private SkillRequestValidator skillRequestValidator;
    @Mock
    private RequestFilter requestFilter;
    @Mock
    private List<RequestFilter> requestFilters;
    private static final long RECOMMENDATION_REQUEST_DTO_ID_ONE = 1L;
    private static final long RECOMMENDATION_REQUEST_ID_ONE = 1L;
    private static final long SKILL_REQUEST_ID_ONE = 1L;
    private static final long SKILL_REQUEST_ID_TWO = 2L;
    private static final long SKILL_REQUEST_ID_THREE = 3L;
    private static final long SKILL_REQUEST_ID_FOUR = 4L;
    private static final String TOO_SERIOUS = "Too serious!";
    private static final RequestStatus REQUEST_STATUS_ACCEPTED = RequestStatus.ACCEPTED;
    private static final RequestStatus REQUEST_STATUS_REJECTED = RequestStatus.REJECTED;

    private RecommendationRequestDto rqd;
    private RecommendationRequest rq;
    private RejectionDto rejection;
    private RequestFilterDto filters;
    private List<RecommendationRequest> requests;

    @BeforeEach
    void setUp() {
        rqd = RecommendationRequestDto.builder()
                .id(RECOMMENDATION_REQUEST_DTO_ID_ONE)
                .skillRequestIds(List.of(SKILL_REQUEST_ID_ONE, SKILL_REQUEST_ID_TWO))
                .build();
        rq = RecommendationRequest.builder()
                .id(RECOMMENDATION_REQUEST_ID_ONE)
                .skills(List.of(SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_ONE)
                                .build(),
                        SkillRequest.builder()
                                .id(SKILL_REQUEST_ID_TWO)
                                .build()))
                .build();
        rejection = RejectionDto.builder()
                .reason(TOO_SERIOUS)
                .build();

        filters = RequestFilterDto.builder()
                .status(REQUEST_STATUS_ACCEPTED)
                .build();
        requests = List.of(RecommendationRequest.builder()
                        .status(REQUEST_STATUS_REJECTED)
                        .skills(List.of(SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_ONE)
                                        .build(),
                                SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_TWO)
                                        .build()))
                        .build(),
                RecommendationRequest.builder()
                        .status(REQUEST_STATUS_ACCEPTED)
                        .skills(List.of(SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_THREE)
                                        .build(),
                                SkillRequest.builder()
                                        .id(SKILL_REQUEST_ID_FOUR)
                                        .build()
                        ))
                        .build());
    }

    @Nested
    class ServiceMethodsTest {

        @Test
        @DisplayName("Check for 5 calls of methods, and 3 returned Objects when service.create(rqd) called")
        public void whenValidDtoPassedItSavedWithItsSkillRequestsThenReturnDto() {
            when(recommendationRequestRepository.save(rq))
                    .thenReturn(rq);
            when(recommendationRequestMapper.toEntity(rqd)).thenReturn(rq);
            when(recommendationRequestService.getAllSkillRequests(rqd)).thenReturn(rq.getSkills());

            recommendationRequestService.create(rqd);

            verify(recommendationRequestValidator)
                    .validateRecommendationRequestMessageNotNull(rqd);
            verify(recommendationRequestValidator)
                    .validateRequesterAndReceiverExists(rqd);
            verify(recommendationRequestValidator)
                    .validatePreviousRequest(rqd);
            verify(skillRequestValidator)
                    .validateSkillsExist(rq.getSkills());
            verify(recommendationRequestRepository)
                    .save(rq);
        }

        @Test
        @DisplayName("Test that recommendationDto returns after calling service.getRequest")
        public void whenValidIdPassedThenReturnFoundDto() {
            rq = recommendationRequestMapper.toEntity(rqd);
            when(recommendationRequestValidator.validateRecommendationRequestExists(RECOMMENDATION_REQUEST_ID_ONE))
                    .thenReturn(rq);
            recommendationRequestService.getRequest(RECOMMENDATION_REQUEST_ID_ONE);
            verify(recommendationRequestValidator)
                    .validateRecommendationRequestExists(RECOMMENDATION_REQUEST_ID_ONE);
        }

        @Test
        @DisplayName("Reject request by passing rejectionDto into method signature return rq")
        public void whenValidRejectionRequestPassedRejectionReasonChangedAndSavedThenReturnDto() {
            when(recommendationRequestValidator
                    .validateRequestStatusNotAcceptedOrDeclined(RECOMMENDATION_REQUEST_ID_ONE)).thenReturn(rq);
            recommendationRequestService.rejectRequest(RECOMMENDATION_REQUEST_ID_ONE, rejection);
            verify(recommendationRequestRepository)
                    .save(rq);
        }

        @Test
        @DisplayName("Check if filters is applied and apply them to stream then return List of filtered rqd's")
        public void whenValidFilterPassedThenReturnFilteredDtoList() {
            customRecommendationRequestService();
            when(recommendationRequestRepository.findAll())
                    .thenReturn(requests);
            when(requestFilter.isApplicable(filters))
                    .thenReturn(true);
            when(requestFilter.applyFilter(any(), eq(filters)))
                    .thenReturn(requests.stream().filter(filter -> filter.getStatus() == REQUEST_STATUS_ACCEPTED));
            List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filters);
            assertEquals(1, result.size());
            verify(recommendationRequestRepository)
                    .findAll();
        }

        @Test
        @DisplayName("Test that service.createSkillRequestDtoBatchSave" +
                " calls for skillRequestRepo.createBatch and calls for specified times")
        public void whenDtoPassedThenSkillRequestsBatchSaved() {
            recommendationRequestService.createSkillRequestDtoBatchSave(rqd);
            verify(skillRequestRepository, times(rqd.getSkillRequestIds().size()))
                    .createBatch(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Test calls for skillRequestRepo.findAllById returns List of skillRequests")
        public void whenDtoPassedThenReturnSkillRequestsList() {
            when(skillRequestRepository.findAllById(anyIterable())).thenReturn(List.of(SkillRequest.builder().build()));
            recommendationRequestService.getAllSkillRequests(rqd);
            verify(skillRequestRepository)
                    .findAllById(anyIterable());
        }
    }

    private void customRecommendationRequestService() {
        requestFilters = List.of(requestFilter);
        recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                recommendationRequestMapper, skillRequestRepository, recommendationRequestValidator, skillRequestValidator,
                requestFilters);
    }
}
