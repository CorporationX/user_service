package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
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
    public void testCreateValidationTrue() {
        skillRequest.setId(id);
        recommendationRequestDto.setSkills(List.of(skillRequest));

        doNothing().when(recommendationRequestDtoValidator).validateAll(any());

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
    public void testGetZeroRequests() {
        Stream<RequestFilter> requestFilterStream = StreamSupport.stream(requestFilterList.spliterator(), false);

        when(requestFilters.stream()).thenReturn(requestFilterStream);

        assertEquals(emptyList, recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    public void testGetRequestNoSuchElement() {
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.getRequest(id));
    }

    @Test
    public void testRejectRequestNoSuchElement() {
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    public void testRejectRequestWrongStatus() {
        recommendationRequest.setStatus(ACCEPTED);
        Optional<RecommendationRequest> recommendationRequestOptional = Optional.of(recommendationRequest);

        when(recommendationRequestRepository.findById(id)).thenReturn(recommendationRequestOptional);

        assertThrows(RuntimeException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }
}