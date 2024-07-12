package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestDtoValidator;
import school.faang.user_service.validator.recommendation.RecommendationRequestIdValidator;
import school.faang.user_service.validator.recommendation.RecommendationRequestIdValidatorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private RecommendationRequestDtoValidator recommendationRequestDtoValidator;

    @Mock
    public List<RequestFilter> requestFilters;

    @Mock
    public RecommendationRequestIdValidator recommendationRequestIdValidator;

    private Long id;
    private RequestFilterDto requestFilterDto;
    private List<RequestFilter> requestFilterList;
    private List<RecommendationRequestDto> emptyList;
    private RejectionDto rejectionDto;
    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private SkillRequest skillRequest;

    @BeforeEach
    public void setUp() {
        id = 1L;

        requestFilterDto = new RequestFilterDto();
        rejectionDto = new RejectionDto();
        recommendationRequest = new RecommendationRequest();
        recommendationRequestDto = new RecommendationRequestDto();
        skillRequest = new SkillRequest();

        requestFilterList = new ArrayList<>();
        emptyList = new ArrayList<>();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test that create method runs successfully when there is no exceptions thrown by RecommendationRequestDtoValidator")
    public void testCreateValidationTrue() {
        skillRequest.setId(id);
        recommendationRequestDto.setSkills(List.of(skillRequest));

        doNothing().when(recommendationRequestDtoValidator).validateMessage(any());
        doNothing().when(recommendationRequestDtoValidator).validateRequestedSkills(any());
        doNothing().when(recommendationRequestDtoValidator).validateRequestTimeDifference(any(), any(), any());
        doNothing().when(recommendationRequestDtoValidator).validateRequesterAndReceiverIds(any(), any());

        when(recommendationRequestMapper.toEntity(any(), any())).thenReturn(new RecommendationRequest());
        when(recommendationRequestRepository.create(any(), any(), any())).thenReturn(id);
        when(skillRequestRepository.create(id, id)).thenReturn(new SkillRequest());
        when(recommendationRequestMapper.toDto(any(), any())).thenReturn(recommendationRequestDto);

        recommendationRequestService.create(recommendationRequestDto);

        verify(recommendationRequestMapper).toEntity(any(), any());
        verify(recommendationRequestRepository).create(any(), any(), any());
        verify(skillRequestRepository).create(id, id);
        verify(recommendationRequestMapper).toDto(any(), any());
    }

    @Test
    @DisplayName("Test that getRequests method returns empty list when empty list was given")
    public void testGetZeroRequests() {
        Stream<RequestFilter> requestFilterStream = StreamSupport.stream(requestFilterList.spliterator(), false);

        when(requestFilters.stream()).thenReturn(requestFilterStream);

        assertEquals(emptyList, recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    @DisplayName("Test that getRequest method throws NoSuchElementException when there is no recommendation requests with given id in database")
    public void testGetRequestNoSuchElement() {
        doNothing().when(recommendationRequestIdValidator).validateId(id);
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.getRequest(id));
    }

    @Test
    @DisplayName("Test that rejectRequest method throws NoSuchElementException when there is no recommendation requests with given id in database")
    public void testRejectRequestNoSuchElement() {
        doNothing().when(recommendationRequestIdValidator).validateId(id);
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    @DisplayName("Test getting RuntimeException when current recommendation request status is not equals PENDING")
    public void testRejectRequestWrongStatus() {
        recommendationRequest.setStatus(ACCEPTED);
        Optional<RecommendationRequest> recommendationRequestOptional = Optional.of(recommendationRequest);

        when(recommendationRequestRepository.findById(id)).thenReturn(recommendationRequestOptional);

        assertThrows(RuntimeException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }
}