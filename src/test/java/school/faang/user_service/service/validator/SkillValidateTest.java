package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validate.SkillValidate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidateTest {

    @InjectMocks
    private SkillValidate skillValidate;

    @Mock
    private SkillRepository skillRepository;


    @Test
    public void testCreateWithBlankTitle() {
        SkillDto skillDto = prepareData(" ");

        assertThrows(DataValidationException.class, () -> skillValidate.validatorSkills(skillDto));
    }

    @Test
    public void testCreateExistingTitle() {
        SkillDto skillDto = prepareData("test");
        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);


        assertThrows(DataValidationException.class, () -> skillValidate.validatorSkills(skillDto));
    }

    private SkillDto prepareData(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(title);
        return skillDto;
    }
}