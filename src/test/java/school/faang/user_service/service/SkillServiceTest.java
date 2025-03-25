package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testDoesSkillExistsRequestDataFromRepositoryReturnsTrue() {
        var skillId = 10L;
        when(skillRepository.existsById(skillId)).thenReturn(true);

        var result = skillService.doesSkillExists(skillId);

        verify(skillRepository, times(1)).existsById(skillId);
        assertTrue(result);
    }
}
