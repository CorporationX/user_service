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

import java.util.Arrays;
import java.util.List;

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
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        assertDoesNotThrow(() -> skillService.create(skillDto));
    }

    @Test
    public void testWithTitleRepetition() {
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testDtoToSkillMapper() {
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        Skill skill = skillMapper.DtoToSkill(skillDto);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testSkillToDtoMapper() {
        Skill skill = createSkill(1L, "firstTitle");
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        SkillDto skillDto = skillMapper.skillToDto(skill);

        assertEquals(skillDto.getId(), skill.getId());
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testCreateSkill() {
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        Skill skill = createSkill(1L, "firstTitle");

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
        SkillDto skillDto = createSkillDto(1L, "firstTitle");
        Skill skill = createSkill(1L, "firstTitle");

        when(skillMapper.DtoToSkill(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.skillToDto(skill)).thenReturn(skillDto);

        skillService.create(skillDto);

        verify(skillMapper).DtoToSkill(skillDto);
        verify(skillMapper).skillToDto(skill);
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;

        Skill firstSkill = createSkill(1L, "firstTitle");
        Skill secondSkill = createSkill(2L, "secondTitle");
        List<Skill> skillList = Arrays.asList(firstSkill, secondSkill);

        SkillDto firstSkillDto = createSkillDto(1L, "firstTitle");
        SkillDto secondSkillDto = createSkillDto(2L, "secondTitle");
        List<SkillDto> skillDtoList = Arrays.asList(firstSkillDto, secondSkillDto);

        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);
        when(skillMapper.skillToDto(firstSkill)).thenReturn(firstSkillDto);
        when(skillMapper.skillToDto(secondSkill)).thenReturn(secondSkillDto);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(skillDtoList, result);

        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).skillToDto(firstSkill);
        verify(skillMapper).skillToDto(secondSkill);
    }

    private SkillDto createSkillDto(long id, String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(id);
        skillDto.setTitle(title);

        return skillDto;
    }

    private Skill createSkill(long id, String title) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setTitle(title);

        return skill;
    }
}
