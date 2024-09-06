package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.skill.SkillNotValidException;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.SkillRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillRequestValidatorTest {
    @InjectMocks
    private SkillRequestValidator skillRequestValidator;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    private List<SkillDto> skills;

    @BeforeEach
    void init() {
        skills = new ArrayList<>();
        skills.add(new SkillDto());
    }

    @Test
    public void validateSkillsExistThrowExceptionTest() {
        when(skillRequestRepository.findById(skills.get(0).getId())).thenReturn(Optional.empty());

        assertThrows(SkillNotValidException.class, () -> skillRequestValidator.validateSkillsExist(skills));
    }

    @Test
    public void validateSkillsExistPositiveTest() {
        skills.get(0).setId(1L);
        when(skillRequestRepository.findById(skills.get(0).getId())).thenReturn(Optional.of(new SkillRequest()));

        assertDoesNotThrow(() -> skillRequestValidator.validateSkillsExist(skills));
    }
}
