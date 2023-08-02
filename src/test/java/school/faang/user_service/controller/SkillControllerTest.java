package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    public SkillController controller;
    @Mock
    private SkillService skillService;

    long userId = 1L;

    @Test
    void testCreate() {
        SkillDto skill = new SkillDto(1L, "Programming");
        controller.create(skill);
        Mockito.verify(skillService, Mockito.times(1)).create(skill);
    }

    @Test
    void getUserSkills() {
        controller.getUserSkills(userId);
        Mockito.verify(skillService).getUserSkills(userId);
    }

    @Test
    void testThrowsDataValidationExceptionOnLength() {
        SkillDto skill = new SkillDto(1L, "Programming1hgfjsadgjkashdkjashlfakhf" +
                "jkdsgfkadhfdlhfldkhgsahgkfaghfhsdkfhsdlkfhskfhkjfsfdfgffhhjjghfgddhhgfdsrytytgfdsfadfafsdggagsfg");
        String message = "Skill's title length can't be more than 64 symbols";
        DataValidationException dataValidationException = Assertions
                .assertThrows(DataValidationException.class, () -> controller.create(skill));
        Assertions.assertEquals(message, dataValidationException.getMessage());
    }

    @Test
    void testThrowsDataValidationExceptionOnBlankTitle() {
        SkillDto skill = new SkillDto(1L, "       ");
        String message = "Skill can't be created with empty name";
        DataValidationException dataValidationException = Assertions
                .assertThrows(DataValidationException.class, () -> controller.create(skill));
        Assertions.assertEquals(message, dataValidationException.getMessage());
    }

    @Test
    void testGetOfferedSkills() {
        controller.getOfferedSkills(userId);
        Mockito.verify(skillService).getOfferedSkills(userId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = 1L;
        controller.acquireSkillFromOffers(skillId, userId);
        Mockito.verify(skillService).acquireSkillFromOffers(skillId, userId);
    }
}