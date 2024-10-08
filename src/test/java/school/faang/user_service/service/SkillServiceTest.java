package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillValidator serviceValidator;

    private List<String> titleSkills;
    private List<Skill> skills;
    private long skillId;
    private long userId;
    private long goalId;

    @BeforeEach
    public void setUp() {
        titleSkills = List.of("title");
        skills = List.of(new Skill());
        skillId = 1;
        userId = 1;
        goalId = 1;
    }

    @Test
    public void getSkillsByTitle() {
        Mockito.doNothing().when(serviceValidator).validateSkill(titleSkills, skillRepository);
        Mockito.when(skillRepository.findByTitleIn(titleSkills)).thenReturn(skills);
        var result = skillService.getSkillsByTitle(titleSkills);
        assertEquals(result, skills);
    }

    @Test
    public void assignSkillToUser() {
        skillService.assignSkillToUser(skillId, userId);
        Mockito.verify(skillRepository).assignSkillToUser(skillId, userId);
    }

    @Test
    public void deleteSkillFromGoal() {
        Mockito.when(skillRepository.findSkillsByGoalId(goalId)).thenReturn(skills);
        skillService.deleteSkillFromGoal(goalId);
        Mockito.verify(skillRepository).findSkillsByGoalId(goalId);
        Mockito.verify(skillRepository).deleteAll(skills);
    }
}
