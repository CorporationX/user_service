package school.faang.user_service.recommendationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
//import school.faang.user_service.mapper.recommendation.RecommendationRequestFilterMapperImpl;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.mapper.recommendation.RecommendationRequestRejectionMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.service.filters.CreatedAtFilter;
import school.faang.user_service.service.filters.MessageFilter;
import school.faang.user_service.service.filters.ReceiverNameFilter;
import school.faang.user_service.service.filters.RecommendationIdFilter;
import school.faang.user_service.service.filters.RejectionReasonFilter;
import school.faang.user_service.service.filters.RequesterNameFilter;
import school.faang.user_service.service.filters.StatusFilter;
import school.faang.user_service.service.filters.UpdatedAtFilter;
import school.faang.user_service.util.filter.Filter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    private RecommendationRequestRepository recommendationRequestRepository;
    private SkillRequestRepository skillRequestRepository;
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestRejectionMapperImpl recommendationRequestRejectionMapper;
    private List<Filter<RequestFilterDto, RecommendationRequest>> filters;
    private RecommendationRequestService recommendationRequestService;
    private Filter<RequestFilterDto, RecommendationRequest> createdAtFilter;
    private Filter<RequestFilterDto, RecommendationRequest> messageFilter;
    private Filter<RequestFilterDto, RecommendationRequest> receiverNameFilter;
    private Filter<RequestFilterDto, RecommendationRequest> recommendationIdFilter;
    private Filter<RequestFilterDto, RecommendationRequest> rejectionReasonFilter;
    private Filter<RequestFilterDto, RecommendationRequest> requesterNameFilter;
    private Filter<RequestFilterDto, RecommendationRequest> statusFilter;
    private Filter<RequestFilterDto, RecommendationRequest> updatedAtFilter;
    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;

    @BeforeEach
    public void init() {
        recommendationRequestRepository = mock(RecommendationRequestRepository.class);
        skillRequestRepository = mock(SkillRequestRepository.class);
        recommendationRequestMapper = mock(RecommendationRequestMapperImpl.class);
        recommendationRequestRejectionMapper = mock(RecommendationRequestRejectionMapperImpl.class);
        createdAtFilter = mock(CreatedAtFilter.class);
        messageFilter = mock(MessageFilter.class);
        receiverNameFilter = mock(ReceiverNameFilter.class);
        recommendationIdFilter = mock(RecommendationIdFilter.class);
        rejectionReasonFilter = mock(RejectionReasonFilter.class);
        requesterNameFilter = mock(RequesterNameFilter.class);
        statusFilter = mock(StatusFilter.class);
        updatedAtFilter = mock(UpdatedAtFilter.class);
        filters = List.of(createdAtFilter, messageFilter, receiverNameFilter, recommendationIdFilter, rejectionReasonFilter, requesterNameFilter, statusFilter, updatedAtFilter);
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository
                , skillRequestRepository
                , recommendationRequestMapper
                , recommendationRequestRejectionMapper
                , filters);
    }

    @Test
    public void testValidateReceiverExistence() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        when(recommendationRequestRepository.existsById(any())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateRequesterExistence() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(-2L);
        requestDto.setReceiverId(-1L);
        when(recommendationRequestRepository.existsById(-1L)).thenReturn(true);
        when(recommendationRequestRepository.existsById(-2L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateRequestsPeriod() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateSkillsExistence() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        requestDto.setUpdatedAt(LocalDateTime.of(2024, Month.NOVEMBER, 1, 13, 6));
        requestDto.setSkillsIds(List.of(1L, 2L));
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testRecommendationRequestDtoCreate() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        requestDto.setUpdatedAt(LocalDateTime.of(2024, Month.NOVEMBER, 1, 13, 6));
        requestDto.setSkillsIds(List.of(1L, 2L));
        requestDto.setMessage("Test");
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        when(skillRequestRepository.findAllById(any())).thenReturn(List.of(new SkillRequest(), new SkillRequest()));
        when(recommendationRequestRepository.getReferenceById(any())).thenReturn(new RecommendationRequest());
        when(skillRequestRepository.findAllById(any())).thenReturn(List.of(new SkillRequest(), new SkillRequest()));
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setMessage(requestDto.getMessage());
        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);
        when(recommendationRequestMapper.ToEntity(any())).thenReturn(recommendationRequest);
        recommendationRequestService.create(requestDto);
        verify(recommendationRequestRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testGetRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        when(recommendationRequestRepository.getReferenceById(any())).thenReturn(recommendationRequest);
        recommendationRequestService.getRequest(1L);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    public void testGetRequestValidation() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(-1);
        when(recommendationRequestRepository.getReferenceById(any())).thenReturn(recommendationRequest);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.getRequest(1L));
    }

    @Test
    public void testRequestsByFilter() {
        RecommendationRequest firstRequest = mock(RecommendationRequest.class);
        firstRequest.setId(1L);
        firstRequest.setMessage("Test");
        firstRequest.setSkills(List.of(new SkillRequest()));
        RecommendationRequest secondRequest = mock(RecommendationRequest.class);
        secondRequest.setId(2L);
        secondRequest.setMessage("Fail");
        secondRequest.setSkills(List.of(new SkillRequest()));

        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRecommendationId(1L);
        filterDto.setMessage("Test");

        List<RecommendationRequest> list = List.of(firstRequest, secondRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(list);
        filters.forEach(filter -> when(filters.get(filters.indexOf(filter)).isApplicable(filterDto)).thenReturn(true));
        filters.forEach(filter -> when(filters.get(filters.indexOf(filter)).apply(any(), any())).thenReturn(Stream.of(new RecommendationRequest())));
        List<RecommendationRequestDto> resultList = recommendationRequestService.getRequestsByFilter(filterDto);
        verify(messageFilter, times(1)).isApplicable(filterDto);
        verify(messageFilter, times(1)).apply(any(), any());
        verify(recommendationIdFilter, times(1)).isApplicable(filterDto);
        verify(recommendationIdFilter, times(1)).apply(any(), any());
    }

    private RecommendationRequestDto validateUserExistenceAndPeriod() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        LocalDateTime createdDate = LocalDateTime.of(2024, Month.FEBRUARY, 1, 13, 7);
        LocalDateTime updatedDate = LocalDateTime.of(2024, Month.AUGUST, 1, 13, 6);
        requestDto.setCreatedAt(createdDate);
        requestDto.setUpdatedAt(updatedDate);
        when(recommendationRequestRepository.existsById(any())).thenReturn(true);
        return requestDto;
    }

    @Test
    public void testRejectRequestException() {
        RejectionDto rejectionDto = new RejectionDto();
        when(recommendationRequestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(1L, rejectionDto));
    }

    @Test
    public void testRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto();
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        when(recommendationRequestRepository.findById(any())).thenReturn(Optional.of(new RecommendationRequest()));
        recommendationRequestService.rejectRequest(1L, rejectionDto);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
    }
}
