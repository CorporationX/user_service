package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Test
    void findAllByUserId() {
        long userId = 42L;
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        List<Skill> expectedSkills = List.of(skill1, skill2);
        when(skillRepository.findAllByUserId(userId)).thenReturn(expectedSkills);

        List<Skill> result = skillService.findAllByUserId(userId);

        assertEquals(expectedSkills, result);
        verify(skillRepository).findAllByUserId(userId);
    }
}