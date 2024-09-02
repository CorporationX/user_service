package school.faang.user_service.validation.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testReceiverUserDoesNotExist() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void test180DaysForNewRequestNotPassed() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()))
                .thenReturn(Optional.of(foundRecommendationRequest));

        assertThrows(IllegalArgumentException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testCheckIFNotAllSkillRequestsExistInDB() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()))
                .thenReturn(Optional.empty());
        when(skillRequestRepository.existsById(Mockito.anyLong())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> requestValidator.validateRecommendationRequest(recommendationRequestDto));
    }

    @Test
    void testValidateRecommendationRequest() {
        when(userRepository.existsById(recommendationRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(recommendationRequestDto.getReceiverId())).thenReturn(true);
        when(requestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()))
                .thenReturn(Optional.empty());
        when(skillRequestRepository.existsById(Mockito.anyLong())).thenReturn(true);

        requestValidator.validateRecommendationRequest(recommendationRequestDto);

        verify(userRepository, times(1)).existsById(recommendationRequestDto.getRequesterId());
        verify(userRepository, times(1)).existsById(recommendationRequestDto.getReceiverId());
        verify(requestRepository, times(1))
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId());
        verify(skillRequestRepository, times(3)).existsById(Mockito.anyLong());
    }
}
