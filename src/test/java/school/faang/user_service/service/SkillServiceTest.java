package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    SkillDto skillDto = new SkillDto(1L, "flexibility");

    @Test
    void testCreateExistingSkill() {
        Mockito.when(skillRepository.existsByTitle(Mockito.anyString()))
                .thenReturn(true);
        assertTrue(skillRepository.existsByTitle(Mockito.anyString()));
        assertThrows(DataValidationException.class,
                () -> skillService.create(new SkillDto(1L, "flexibility")));
    }

    @Test
    void testCreateSkill() {
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void testGetUserSkillsUserIsNotExist(){
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> skillService.getUserSkills(1L));
    }

    @Test
    void testCallMethod
}