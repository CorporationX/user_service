package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.goal.SkillService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    private Skill skill;
    private User user;
    private List<Skill> skills;
    private List<User> users;
    private Long userId;
    private Long goalId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        goalId = 1L;

        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Test Skill");

        user = new User();
        user.setId(1L);
        user.setSkills(new ArrayList<>(List.of(skill)));

        skills = new ArrayList<>(List.of(skill));
        users = new ArrayList<>(List.of(user));
    }

    @Test
    @DisplayName("Test if all skills exist by title")
    @Transactional
    public void testExistsByTitle() {
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);

        boolean result = skillService.existsByTitle(skills);

        verify(skillRepository, times(1)).existsByTitle(skill.getTitle());
        assertTrue(result, "All skills should exist by title");
    }

    @Test
    @DisplayName("Test if some skills do not exist by title")
    @Transactional
    public void testExistsByTitleWhenNotAllExist() {
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(false);

        boolean result = skillService.existsByTitle(skills);

        verify(skillRepository, times(1)).existsByTitle(skill.getTitle());
        assertFalse(result, "Not all skills exist by title");
    }

    @Test
    @DisplayName("Test skill creation and assignment to user")
    @Transactional
    public void testCreateSkillsForUser() {
        skillService.create(skills, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skill.getId(), userId);
    }

    @Test
    @DisplayName("Test adding skills to users based on goalId")
    @Transactional
    public void testAddSkillsToUsers() {
        when(skillRepository.findSkillsByGoalId(goalId)).thenReturn(skills);

        skillService.addSkillToUsers(users, goalId);

        verify(skillRepository, times(1)).findSkillsByGoalId(goalId);
        assertTrue(user.getSkills().containsAll(skills), "User should have the new skills added");
    }
}
