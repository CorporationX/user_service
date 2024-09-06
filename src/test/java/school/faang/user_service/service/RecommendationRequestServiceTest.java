package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.recommendation.SkillRequestValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private RecommendationRequestDto rqd;

    @BeforeEach
    void setUp() {
        rqd = new RecommendationRequestDto();
    }

    @Test
    public void createSaveReturnsDtoTest() {
        rqd.setId(2L);
        List<SkillDto> skills = Arrays.asList(new SkillDto(), new SkillDto());
        rqd.setSkills(skills);
        rqd.getSkills().get(0).setId(5L);
        rqd.getSkills().get(1).setId(3L);
        RecommendationRequest rq = recommendationRequestMapper.toEntity(rqd);

        when(recommendationRequestRepository.save(rq))
                .thenReturn(rq);
        recommendationRequestService.create(rqd);
        verify(recommendationRequestValidator, times(1))
                .validateRequesterAndReceiverExists(rqd);
        verify(recommendationRequestValidator, times(1))
                .validatePreviousRequest(rqd);
        verify(skillRequestValidator, times(1))
                .validateSkillsExist(rqd.getSkills());
        verify(skillRequestRepository, times(rqd.getSkills().size()))
                .create(anyLong(), anyLong());
    }

    @Test
    public void getRequestTestReturnsDto() {
        RecommendationRequest rq = recommendationRequestMapper.toEntity(rqd);

        when(recommendationRequestValidator.validateRecommendationRequestExists(anyLong()))
                .thenReturn(rq);
        recommendationRequestService.getRequest(anyLong());
        verify(recommendationRequestValidator, times(1))
                .validateRecommendationRequestExists(anyLong());
    }

    @Test
    public void rejectRequestReturnDtoTest() {
        RecommendationRequest rq = new RecommendationRequest();
        RejectionDto rejection = new RejectionDto();
        rejection.setReason("Too serious!");

        when(recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(anyLong()))
                .thenReturn(rq);
        recommendationRequestService.rejectRequest(anyLong(), rejection);
        verify(recommendationRequestRepository, times(1))
                .save(rq);
    }

    @Test
    public void getRequestsReturnFilteredDtoTest() {
        customRecommendationRequestService();

        RequestFilterDto filters = new RequestFilterDto();
        filters.setStatus(RequestStatus.ACCEPTED);

        RecommendationRequest rq1 = new RecommendationRequest();
        rq1.setStatus(RequestStatus.REJECTED);
        RecommendationRequest rq2 = new RecommendationRequest();
        rq2.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> requests = List.of(rq1, rq2);

        when(recommendationRequestRepository.findAll())
                .thenReturn(requests);
        when(requestFilter.isApplicable(filters))
                .thenReturn(true);
        when(requestFilter.applyFilter(any(), eq(filters)))
                .thenReturn(requests.stream().filter(x -> x.getStatus() == RequestStatus.ACCEPTED));
        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filters);

        assertEquals(1, result.size());
        assertEquals(rq2.getStatus(), result.get(0).getStatus());
        verify(recommendationRequestRepository, times(1))
                .findAll();
    }

    private void customRecommendationRequestService() {
        requestFilters = List.of(requestFilter);
        recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                recommendationRequestMapper, skillRequestRepository, recommendationRequestValidator, skillRequestValidator,
                requestFilters);
    }
}
