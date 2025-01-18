package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findSkillById_ShouldReturnSkill() {
        Skill skill = new Skill();
        when(skillRepository.findById(anyLong())).thenReturn(Optional.of(skill));

        Optional<Skill> result = skillService.findSkillById(1L);

        assertTrue(result.isPresent());
        assertEquals(skill, result.get());
    }

    @Test
    void findSkillById_ShouldReturnEmpty_WhenSkillNotFound() {
        when(skillRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Skill> result = skillService.findSkillById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void assignSkillsFromGoalToUsers_ShouldInvokeRepositoryAssignSkillToUser() {
        User user = new User();
        user.setId(1L);
        List<User> users = List.of(user);

        doNothing().when(skillRepository).assignSkillToUser(anyLong(), anyLong());

        skillService.assignSkillsFromGoalToUsers(1L, users);

        verify(skillRepository, times(1)).assignSkillToUser(1L, 1L);
    }
}