package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SkillServiceTest {
    private SkillRepository skillRepository;
    private SkillMapper skillMapper;
    private SkillService skillService;


    @BeforeEach
    public void setUp() {
        skillRepository = Mockito.mock(SkillRepository.class);
        skillMapper = Mockito.mock(SkillMapper.class);
        skillService = new SkillService(skillRepository, skillMapper);

        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");

        Skill skill = new Skill();
        skill.setTitle("Java");


        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto createdSkill = skillService.create(skillDto);

        assertNotNull(createdSkill);
        assertEquals("Java", createdSkill.getTitle());
    }

    @Test
    public void testCreateSkill_AlreadyExists() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("Java");

        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }
}
