package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.goal.SkillService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillServiceTest {

    @InjectMocks
    SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testExistsByTitleIfTitleNotExist() {
        String firstSkillTitle = "firstTitle";
        String secondSkillTitle = "secondTitle";
        Skill firstSkill = new Skill();
        firstSkill.setTitle(firstSkillTitle);
        Skill secondSkill = new Skill();
        secondSkill.setTitle(secondSkillTitle);
        List<Skill> skills = Arrays.asList(firstSkill, secondSkill);

        when(skillRepository.existsByTitle(firstSkill.getTitle())).thenReturn(true);
        when(skillRepository.existsByTitle(secondSkill.getTitle())).thenReturn(false);

        assertFalse(skillService.existsByTitle(skills));
    }

    @Test
    public void testExistsByTitleWhenValid() {
        String firstSkillTitle = "firstTitle";
        String secondSkillTitle = "secondTitle";
        Skill firstSkill = new Skill();
        firstSkill.setTitle(firstSkillTitle);
        Skill secondSkill = new Skill();
        secondSkill.setTitle(secondSkillTitle);
        List<Skill> skills = Arrays.asList(firstSkill, secondSkill);

        when(skillRepository.existsByTitle(firstSkill.getTitle())).thenReturn(true);
        when(skillRepository.existsByTitle(secondSkill.getTitle())).thenReturn(true);

        assertTrue(skillService.existsByTitle(skills));
    }

    @Test
    public void testCreateWhenValid() {
        Long userId = 1L;
        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);
        List<Skill> skills = Arrays.asList(firstSkill, secondSkill);

        skillService.create(skills, userId);

        verify(skillRepository, times(1)).assignSkillToUser(firstSkill.getId(), userId);
        verify(skillRepository, times(1)).assignSkillToUser(secondSkill.getId(), userId);
    }

    @Test
    public void testFindSkillsByGoalIdWhenValid() {
        Long goalId = 1L;
        List<Skill> skillsRepository = Arrays.asList(new Skill(), new Skill());

        when(skillRepository.findSkillsByGoalId(goalId)).thenReturn(skillsRepository);
        List<Skill> valid = skillService.findSkillsByGoalId(goalId);

        assertEquals(skillsRepository, valid);
    }

    @Test
    public void testAddSkillToUsersWhenValid() {
        Long goalId = 1L;
        List<User> users = Arrays.asList(new User(), new User());

        when(skillService.findSkillsByGoalId(goalId)).thenReturn(new ArrayList<>());

        skillService.addSkillToUsers(users, goalId);
        verify(skillService, times(users.size())).findSkillsByGoalId(goalId);
    }
}