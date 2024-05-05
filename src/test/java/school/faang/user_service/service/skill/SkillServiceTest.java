package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    SkillService skillService;

    @Mock
    SkillRepository skillRepository;

    @Test
    void testCheckSkillInDBWhenFirstSkillExistButSecondDoesntExist() {
        List<Skill> skills = init(true, false);

        assertEquals(false, skillService.checkSkillsInDB(skills));
    }

    @Test
    void testCheckSkillInDBWhenAllSkillsExist() {
        List<Skill> skills = init(true, true);

        assertEquals(true, skillService.checkSkillsInDB(skills));
    }

    @Test
    void testCheckAmountSkillsInDB() {
        List<Long> skillIds = List.of(1L,2L,3L,4L);

        Mockito.when(skillRepository.countExisting(skillIds)).thenReturn(4);

        assertEquals(4, skillService.checkAmountSkillsInDB(skillIds));
    }

    @Test
    void testAssignSkillToUser() {
        skillService.assignSkillToUser(1L, 1L);
        Mockito.verify(skillRepository).assignSkillToUser(1L, 1L);
    }

    private List<Skill> init(boolean param1, boolean param2) {
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setTitle("Skill1");
        secondSkill.setTitle("Skill2");
        List<Skill> skills = List.of(firstSkill, secondSkill);

        Mockito.when(skillRepository.existsByTitle(firstSkill.getTitle())).thenReturn(param1);
        Mockito.when(skillRepository.existsByTitle(secondSkill.getTitle())).thenReturn(param2);

        return skills;
    }
}