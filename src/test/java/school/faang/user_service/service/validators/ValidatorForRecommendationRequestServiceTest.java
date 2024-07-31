package school.faang.user_service.service.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorForRecommendationRequestServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestRepository requestRepository;

    @InjectMocks
    private ValidatorForRecommendationRequestService validator;

    @Test
    void testValidatorDataIdRequesterIsIdReceiver() {
        var dto = createDto();
        dto.setRecieverId(dto.getRequesterId());

        assertThrows(DataValidationException.class, () -> validator.validatorData(dto));
    }

    @Test
    void testValidatorDataRequesterNotExists() {
        var dto = createDto();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> validator.validatorData(dto));
        verify(userRepository).existsById(dto.getRequesterId());
    }

    @Test
    void testValidatorDataReceiverNotExists() {
        var dto = createDto();
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getRecieverId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> validator.validatorData(dto));
        verify(userRepository).existsById(dto.getRecieverId());
    }

    @Test
    void testValidatorDataWithUpdatedAtLessSixMonths() {
        var dto = createDto();
        dto.setUpdatedAt(LocalDateTime.now());
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(dto.getRecieverId())).thenReturn(true);
        when(requestRepository.existsById(dto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> validator.validatorData(dto));
    }

    private RecommendationRequestDto createDto() {
        var dto = new RecommendationRequestDto();
        dto.setId(1L);
        dto.setRecieverId(2L);
        dto.setRequesterId(3L);
        dto.setMessage("message");
        dto.setSkillDtos(List.of(
                new SkillRequestDto(1L, 2L, 3L),
                new SkillRequestDto(2L, 2L, 2L))
        );
        return dto;
    }
}