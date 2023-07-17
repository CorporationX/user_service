package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

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

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    RecommendationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(new SkillRequest(1L, new RecommendationRequest(), Skill.builder().id(1).build())))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
    }

    @Test
    void testValidationExistByIdThrowNegative() {
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testValidationExistByIdThrowPositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testValidationRequestDateNegative() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(5));
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testValidationRequestDatePositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testValidationExistSkillNegative() {
        requestDto.setCreatedAt(LocalDateTime.now());
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testValidationExistSkillPositive() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

}