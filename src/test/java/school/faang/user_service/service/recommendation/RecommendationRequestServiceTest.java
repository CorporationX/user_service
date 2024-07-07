package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<RequestFilter> requestFilters;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEmplyMessage() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("");

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testCreateNotExistIds() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setRequesterId(1L);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testTimeRequestDiference() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setRequesterId(1L);

        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);

        LocalDateTime currentRequestTime = LocalDateTime.now();
        LocalDateTime latestRequestTime = currentRequestTime.plusMonths(5);
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequestDto.setCreatedAt(currentRequestTime);
        recommendationRequest.setCreatedAt(latestRequestTime);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testAllSkillExists() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setRequesterId(1L);
        Skill skill = new Skill();
        skill.setTitle("Test title");
        recommendationRequestDto.setSkills(List.of(new SkillRequest(1L, new RecommendationRequest(), skill)));

        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);

        LocalDateTime currentRequestTime = LocalDateTime.now();
        LocalDateTime latestRequestTime = currentRequestTime.plusMonths(7);
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequestDto.setCreatedAt(currentRequestTime);
        recommendationRequest.setCreatedAt(latestRequestTime);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        when(skillRepository.existsByTitle(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    public void testGetZeroRequests() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        List<RecommendationRequestDto> emptyList = new ArrayList<>();

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