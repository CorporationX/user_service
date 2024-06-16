package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    private final long TEST_ID = 1L;
    private final String TEST_SKILL = "Java";
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    private SkillDto skill;

    @BeforeEach
    public void init() {
        skill = new SkillDto();
        skill.setId(TEST_ID);
        skill.setTitle(TEST_SKILL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void skillCreatedWithEmptyTitle(String title) {
        skill.setTitle(title);
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill));
    }

    @Test
    public void skillAlreadyExists() {
        Mockito.when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);
        assertThrows(RuntimeException.class, () -> skillValidator.validateSkill(skill));
    }
}
