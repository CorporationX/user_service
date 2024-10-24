package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.model.dto.RecommendationRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.SkillRequest;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.RecommendationRequestRepository;
import school.faang.user_service.repository.SkillRequestRepository;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.service.impl.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilters;

    @Mock
    private RecommendationRequestedEventPublisher recommendationRequestedEventPublisher;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_ShouldSaveAndReturnDto() {
        RecommendationRequestDto requestDto = mock(RecommendationRequestDto.class);
        RecommendationRequest recommendationRequestEntity = mock(RecommendationRequest.class);
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(new Skill());
        List<SkillRequest> skillRequests = List.of(skillRequest);

        when(skillRequestRepository.findAllById(any())).thenReturn(skillRequests);
        when(recommendationRequestMapper.mapToEntity(requestDto)).thenReturn(recommendationRequestEntity);
        when(recommendationRequestMapper.mapToDto(recommendationRequestEntity)).thenReturn(requestDto);
        when(recommendationRequestRepository.save(recommendationRequestEntity)).thenReturn(recommendationRequestEntity);

        RecommendationRequestDto result = recommendationRequestService.create(requestDto);

        verify(recommendationRequestValidator).isUsersInDb(requestDto);
        verify(recommendationRequestValidator).isSkillsInDb(requestDto);
        verify(recommendationRequestValidator).isRequestAllowed(requestDto);
        verify(recommendationRequestRepository).save(recommendationRequestEntity);
        assertEquals(requestDto, result);
    }

    @Test
    void testGetRequests_ShouldReturnFilteredRequests() {
        RecommendationRequestFilterDto filterDto = mock(RecommendationRequestFilterDto.class);
        RecommendationRequest recommendationRequest = mock(RecommendationRequest.class);
        List<RecommendationRequest> recommendationRequests = List.of(recommendationRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);

        when(recommendationRequestMapper.mapToDto(anyList())).thenReturn(List.of(mock(RecommendationRequestDto.class)));

        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        verify(recommendationRequestRepository).findAll();
        assertNotNull(result);
    }

    @Test
    void testGetRequest_ShouldReturnRequestDto() {
        Long id = 1L;
        RecommendationRequest recommendationRequest = mock(RecommendationRequest.class);
        RecommendationRequestDto recommendationRequestDto = mock(RecommendationRequestDto.class);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.mapToDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(id);

        verify(recommendationRequestRepository).findById(id);
        verify(recommendationRequestMapper).mapToDto(recommendationRequest);
        assertEquals(recommendationRequestDto, result);
    }

    @Test
    void testGetRequest_ShouldThrowExceptionWhenNotFound() {
        Long id = 1L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.getRequest(id));
    }

    @Test
    void testRejectRequest_ShouldUpdateAndReturnRejectionDto() {
        Long id = 1L;
        RejectionDto rejectionDto = mock(RejectionDto.class);
        RecommendationRequest recommendationRequest = mock(RecommendationRequest.class);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.mapToRejectionDto(recommendationRequest)).thenReturn(rejectionDto);

        RejectionDto result = recommendationRequestService.rejectRequest(id, rejectionDto);

        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationRequest).setStatus(RequestStatus.REJECTED);
        verify(recommendationRequest).setRejectionReason(rejectionDto.getRejectionReason());
        assertEquals(rejectionDto, result);
    }

    @Test
    void testRejectRequest_ShouldThrowExceptionWhenNotFound() {
        Long id = 1L;
        RejectionDto rejectionDto = mock(RejectionDto.class);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }
}
