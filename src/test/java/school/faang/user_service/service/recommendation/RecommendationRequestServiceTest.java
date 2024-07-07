package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

//    @Test
//    public void testTimeRequestDiference() {
//        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
//        recommendationRequestDto.setMessage("Test message");
//
//        LocalDateTime currentRequestTime = LocalDateTime.now();
//        LocalDateTime latestRequestTime = currentRequestTime.plusMonths(7);
//        RecommendationRequest recommendationRequest = new RecommendationRequest();
//        recommendationRequest.setCreatedAt(latestRequestTime);
//        //Optional<LocalDateTime> latestRequestTimeOptional = Optional.of(latestRequestTime);
//
//        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
//                .thenReturn(Optional.of(recommendationRequest));
//
////        LocalDateTime currentRequestTime = recommendationRequestDto.getCreatedAt();
////        LocalDateTime latestRequestTime = recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
////                recommendationRequestDto.getReceiverId()).get().getCreatedAt();
//
//        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
//    }
}