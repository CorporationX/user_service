package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.mappers.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.SkillService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper;

    @Test
    public void testWithCorrectValidateTitleNonRepetition() {
        SkillDto skillDto = createSkillDto();
        assertDoesNotThrow(() -> skillService.create(skillDto));
    }

    @Test
    public void testWithTitleRepetition() {
        SkillDto skillDto = createSkillDto();
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testDtoToSkillMapper() {
        SkillDto skillDto = createSkillDto();
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        Skill skill = skillMapper.DtoToSkill(skillDto);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testSkillToDtoMapper() {
        Skill skill = createSkill();
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        SkillDto skillDto = skillMapper.skillToDto(skill);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testCreateSkill() {
        SkillDto skillDto = createSkillDto();

        Skill skill = createSkill();

        when(skillMapper.DtoToSkill(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.skillToDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertEquals(skillDto, result);

        verify(skillMapper).DtoToSkill(skillDto);
        verify(skillRepository).save(skill);
        verify(skillMapper).skillToDto(skill);
    }

    @Test
    public void testMapperCall() {
        SkillDto skillDto = createSkillDto();

        Skill skill = createSkill();

        when(skillMapper.DtoToSkill(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.skillToDto(skill)).thenReturn(skillDto);

        skillService.create(skillDto);

        verify(skillMapper).DtoToSkill(skillDto);
        verify(skillMapper).skillToDto(skill);
    }

    private SkillDto createSkillDto() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("title");

        return skillDto;
    }

    private Skill createSkill() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");

        return skill;
    }
}
