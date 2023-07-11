package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    void createReturnsSkillDto(){
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = SkillMapper.INSTANCE.skillToEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("title", result.getTitle());

        verify(skillRepository).existsByTitle(skillDto.getTitle());
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void createReturnsValidationException() {
        SkillDto skillDto = new SkillDto(1L, "title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> {
            skillService.create(skillDto);
        });

        verify(skillRepository).existsByTitle(skillDto.getTitle());
    }
}