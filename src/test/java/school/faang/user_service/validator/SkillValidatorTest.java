package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SkillValidatorTest {
    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;
    RecommendationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillsId(List.of(1L))
                .requesterId(1L)
                .receiverId(1L)
                .build();
    }

    @Test
    void testValidationExistSkillNegative() {
        assertThrows(DataValidationException.class, () -> skillValidator.validationExistSkill(requestDto));
    }

    @Test
    void testValidationExistSkillPositive() {
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> skillValidator.validationExistSkill(requestDto));
    }
}