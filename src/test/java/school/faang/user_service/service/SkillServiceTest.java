package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    SkillService service;

    @Mock
    SkillRepository repository;

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        Mockito.when(repository.findAllByUserId(userId)).thenReturn(List.of(new Skill()));
        service.getUserSkills(userId);
        Mockito.verify(repository).findAllByUserId(userId);
    }

    @Test
    void testThrown() {
        long userId = 1L;
        Mockito.when(repository.findAllByUserId(userId)).thenReturn(List.of(new Skill()));
        service.getUserSkills(userId);
        Mockito.verify(repository).findAllByUserId(userId);
    }
}