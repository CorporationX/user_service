package school.faang.user_service.service.recommendation_request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.CreateRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    @InjectMocks
    private RecommendationRequestValidator validator;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper mapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Test
    public void testCreateValidationSuccess() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 2L);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(null));

        validator.validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills());

        verify(userRepository, times(2)).existsById(anyLong());
        verify(skillRepository, times(dto.getSkills().size())).existsById(anyLong());
        verify(recommendationRequestRepository).findLatestPendingRequest(anyLong(), anyLong());
    }

    @Test
    public void testCreateValidationEqualsRequesterReceiver() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 1L);

        assertThrows(ValidationException.class, () -> this.validator
                .validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills())
        );
    }

    @Test
    public void testCreateValidationRequesterNotFound() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 2L);

        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);
        assertThrows(ValidationException.class, () -> this.validator
                .validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills())
        );
    }

    @Test
    public void testCreateValidationReceiverNotFound() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 2L);

        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(false);
        assertThrows(ValidationException.class, () -> this.validator
                .validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills())
        );
    }

    @Test
    public void testCreateValidationSkillNotFound() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 2L);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ValidationException.class, () -> this.validator
                .validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills())
        );
    }

    @Test
    public void testCreateValidationLastRequestMoreSixMonthsFailed() {
        CreateRecommendationRequestDto dto = this.makeCreatedDto(1L, 2L);

        RecommendationRequest recommendationRequest = mapper.toEntity(dto);
        recommendationRequest.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(ValidationException.class, () -> this.validator
                .validateCreateRecommendationRequest(mapper.toEntity(dto), dto.getSkills())
        );
    }

    private CreateRecommendationRequestDto makeCreatedDto(Long requesterId, Long receiverId) {
        return new CreateRecommendationRequestDto(
                "Message",
                new ArrayList<>(List.of(1L, 2L)),
                requesterId,
                receiverId
        );
    }
}
