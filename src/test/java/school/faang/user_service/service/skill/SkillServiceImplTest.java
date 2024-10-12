package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillServiceImpl skillService;

    private User user;
    private List<Skill> skills;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        Skill skill1 = new Skill();
        skill1.setId(101L);
        Skill skill2 = new Skill();
        skill2.setId(102L);
        skills = Arrays.asList(skill1, skill2);
    }

    @Test
    public void testAssignSkillsToUser_Success() {
        skillService.assignSkillsToUser(user, skills);

        verify(skillRepository, times(1)).assignSkillToUser(101L, 1L);
        verify(skillRepository, times(1)).assignSkillToUser(102L, 1L);
    }

    @Test
    public void testRemoveSkillsFromUser_Success() {
        when(skillRepository.findUserSkill(101L, 1L)).thenReturn(Optional.of(skills.get(0)));
        when(skillRepository.findUserSkill(102L, 1L)).thenReturn(Optional.of(skills.get(1)));

        skillService.removeSkillsFromUser(user, skills);

        verify(skillRepository, times(1)).delete(skills.get(0));
        verify(skillRepository, times(1)).delete(skills.get(1));
    }

    @Test
    public void testRemoveSkillsFromUser_NoSkillsFound() {
        when(skillRepository.findUserSkill(101L, 1L)).thenReturn(Optional.empty());
        when(skillRepository.findUserSkill(102L, 1L)).thenReturn(Optional.empty());

        skillService.removeSkillsFromUser(user, skills);

        verify(skillRepository, never()).delete(any(Skill.class));
    }
}