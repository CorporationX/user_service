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

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateValidationTrue() {
        Long id = 1L;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(id);
        recommendationRequestDto.setSkills(List.of(skillRequest));

        doNothing().when(recommendationRequestDtoValidator).validateRecommendationRequestDto(any());

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
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        List<RequestFilter> requestFilterList = new ArrayList<>();
        Stream<RequestFilter> requestFilterStream = StreamSupport.stream(requestFilterList.spliterator(), false);
        List<RecommendationRequestDto> emptyList = new ArrayList<>();

        when(requestFilters.stream()).thenReturn(requestFilterStream);

        assertEquals(emptyList, recommendationRequestService.getRequests(requestFilterDto));
    }

    @Test
    public void testGetRequestNoSuchElement() {
        Long id = 1L;

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.getRequest(id));
    }

    @Test
    public void testRejectRequestNoSuchElement() {
        Long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }

    @Test
    public void testRejectRequestWrongStatus() {
        Long id = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setStatus(ACCEPTED);
        Optional<RecommendationRequest> recommendationRequestOptional = Optional.of(recommendationRequest);

        when(recommendationRequestRepository.findById(id)).thenReturn(recommendationRequestOptional);

        assertThrows(RuntimeException.class, () -> recommendationRequestService.rejectRequest(id, rejectionDto));
    }
}