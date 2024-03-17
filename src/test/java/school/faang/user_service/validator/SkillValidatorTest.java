package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @InjectMocks
    SkillValidator skillValidator;
            
    @Mock
    SkillRepository skillRepository;
    
    SkillDto skillDto;
    
    @BeforeEach
    public void setup() {
        skillDto = SkillDto.builder().id(1L).build();
    }
    
    @Test
    public void shouldThrowExceptionOnCreateWhenSkillTitleIsEmpty () {
        skillDto.setTitle("");

        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto.getTitle()));
    }

    @Test
    public void shouldThrowExceptionOnCreateWhenSkillTitleIsNull () {
        skillDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skillDto.getTitle()));
    }

}
