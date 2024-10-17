package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    private static final int SIX_MONTH_IN_DAYS = 180;
    private static final int WEEK = 7;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestValidator requestValidator;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest foundRecommendationRequest;

    @BeforeEach
    public void setup() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .skillsId(new ArrayList<>())
                .requesterId(10L)
                .receiverId(1L)
                .skillsId(List.of(11L, 12L, 14L))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        foundRecommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .createdAt(LocalDateTime.now().plusDays(WEEK))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testRequesterUserDoesNotExist() {
        when(userRepository.existsById(recommendationRequestDto.requesterId())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testReceiverUserDoesNotExist() {
        when(userRepository.existsById(recommendationRequestDto.requesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.receiverId())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void test180DaysForNewRequestNotPassed() {
        when(userRepository.existsById(recommendationRequestDto.requesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.receiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                recommendationRequestDto.receiverId()))
                .thenReturn(Optional.of(foundRecommendationRequest));

        assertThrows(DataValidationException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testCheckIFNotAllSkillRequestsExistInDB() {
        when(userRepository.existsById(recommendationRequestDto.requesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.receiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                recommendationRequestDto.receiverId()))
                .thenReturn(Optional.empty());
        when(skillRequestRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testValidateRecommendationRequest() {
        when(userRepository.existsById(recommendationRequestDto.requesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.receiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                recommendationRequestDto.receiverId()))
                .thenReturn(Optional.empty());
        when(skillRequestRepository.existsById(Mockito.anyLong())).thenReturn(true);

        requestValidator.validateRecommendationRequest(recommendationRequestDto);

        verify(userRepository, times(1)).existsById(recommendationRequestDto.requesterId());
        verify(userRepository, times(1)).existsById(recommendationRequestDto.receiverId());
        verify(requestRepository, times(1))
                .findLatestPendingRequest(recommendationRequestDto.requesterId(),
                        recommendationRequestDto.receiverId());
        verify(skillRequestRepository, times(3)).existsById(Mockito.anyLong());
    }
}
