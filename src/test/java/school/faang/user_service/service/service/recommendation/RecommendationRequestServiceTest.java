package school.faang.user_service.service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("Hello")
                .status(RequestStatus.ACCEPTED)
                .skillIds(List.of(1L))
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
    }

    @Test
    void testThrowValidationExceptionByUserId() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testThrowValidationExceptionBySkillId() {
        Mockito.lenient().when(skillRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testNotThrowValidationException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testNotThrowValidationExceptionByRequestTime() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(5));
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testThrowValidationExceptionByRequestTime() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(10));
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.existsById(2L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }
}