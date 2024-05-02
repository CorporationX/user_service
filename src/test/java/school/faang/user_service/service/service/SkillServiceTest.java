package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
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
    public void testSkillToSkillCandidateDtoMapper() {
        Skill skill = createSkill(1L, "firstTitle");
        SkillMapperImpl skillMapper = new SkillMapperImpl();

        SkillCandidateDto skillCandidateDto = skillMapper.skillToSkillCandidateDto(skill, 1);

        SkillDto expectedSkillDto = new SkillDto(skill.getId(), skill.getTitle());
        SkillCandidateDto expectedSkillCandidateDto = new SkillCandidateDto(expectedSkillDto, 1L);

        assertEquals(expectedSkillCandidateDto, skillCandidateDto);
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

        List<Skill> skillList = createSkillList();
        List<SkillDto> skillDtoList = createSkillDtoList();

        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);
        when(skillMapper.skillToDto(skillList.get(0))).thenReturn(skillDtoList.get(0));
        when(skillMapper.skillToDto(skillList.get(1))).thenReturn(skillDtoList.get(1));

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(skillDtoList, result);

        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).skillToDto(skillList.get(0));
        verify(skillMapper).skillToDto(skillList.get(1));
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;

        List<Skill> skillList = createSkillList();
        List<SkillCandidateDto> skillCandidateDtoList = createSkillCandidateDto();

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillList);
        when(skillRepository.countOffersByUserIdAndSkillId(userId, skillList.get(0).getId())).thenReturn(1L);
        when(skillRepository.countOffersByUserIdAndSkillId(userId, skillList.get(1).getId())).thenReturn(2L);
        when(skillMapper.skillToSkillCandidateDto(skillList.get(0), 1L)).thenReturn(skillCandidateDtoList.get(0));
        when(skillMapper.skillToSkillCandidateDto(skillList.get(1), 2L)).thenReturn(skillCandidateDtoList.get(1));

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(skillCandidateDtoList, result);

        verify(skillRepository).findSkillsOfferedToUser(userId);
        verify(skillRepository).countOffersByUserIdAndSkillId(userId, skillList.get(0).getId());
        verify(skillRepository).countOffersByUserIdAndSkillId(userId, skillList.get(1).getId());
        verify(skillMapper).skillToSkillCandidateDto(skillList.get(0), 1L);
        verify(skillMapper).skillToSkillCandidateDto(skillList.get(1), 2L);
    }

    private List<SkillCandidateDto> createSkillCandidateDto() {
        SkillCandidateDto firstSkillCandidateDto = createSkillCandidateDto(createSkillDto(1L, "firstTitle"), 1L);
        SkillCandidateDto secondSkillCandidateDto = createSkillCandidateDto(createSkillDto(2L, "secondTitle"), 2L);

        return Arrays.asList(firstSkillCandidateDto, secondSkillCandidateDto);
    }

    private List<SkillDto> createSkillDtoList() {
        SkillDto firstSkillDto = createSkillDto(1L, "firstTitle");
        SkillDto secondSkillDto = createSkillDto(2L, "secondTitle");

        return Arrays.asList(firstSkillDto, secondSkillDto);
    }

    private List<Skill> createSkillList() {
        Skill firstSkill = createSkill(1L, "firstTitle");
        Skill secondSkill = createSkill(2L, "secondTitle");

        return Arrays.asList(firstSkill, secondSkill);
    }

    private SkillCandidateDto createSkillCandidateDto(SkillDto skillDto, long offersAmount) {
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        skillCandidateDto.setSkillDto(skillDto);
        skillCandidateDto.setOffersAmount(offersAmount);

        return skillCandidateDto;
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
