package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ListOfSkillsCandidateMapperImpl;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private ListOfSkillsCandidateMapperImpl listOfSkillsCandidateMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testCreate() {
        SkillDto skill = new SkillDto(1L, "title");
        skillService.create(skill);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skill));
    }

    @Test
    public void testGetUserSkills() {
        SkillDto skill = new SkillDto(1L, "title");
        skillService.getUserSkills(skill.getId());
        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(skill.getId());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill firstSkill = new Skill();
        firstSkill.setTitle("title");
        firstSkill.setId(1L);
        List<Skill> listOfOneSkill = new ArrayList<>();
        listOfOneSkill.add(firstSkill);
        Mockito.when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(listOfOneSkill);
        skillService.getOfferedSkills(userId);
        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testMapperSkillToDto() {
        Skill skill = new Skill();
        skill.setTitle("title");
        skill.setId(1L);
        SkillDto skillDto = skillMapper.toDto(skill);
        assertThat(skill.getId() == skillDto.getId()).isTrue();
        assertThat(skill.getTitle().equals(skillDto.getTitle())).isTrue();
    }

    @Test
    public void testMapperDtoToSkill() {
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = skillMapper.toEntity(skillDto);
        assertThat(skill.getTitle().equals(skillDto.getTitle())).isTrue();
        assertThat(skill.getId() == skillDto.getId()).isTrue();
    }

    @Test
    public void testMapperSkillToSkillCandidateDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto expected = new SkillCandidateDto();
        expected.setSkill(new SkillDto(1L, "title"));
        expected.setOffersAmount(1L);
        SkillCandidateDto actual = skillCandidateMapper.skillToSkillCandidateDto(skill);
        assertThat(actual.getSkill().equals(expected.getSkill())).isTrue();
        assertThat(actual.getOffersAmount() == expected.getOffersAmount()).isTrue();
    }

    @Test
    public void testMapperListOfSkillsToCandidateDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto skillCandidateDto = skillCandidateMapper.skillToSkillCandidateDto(skill);
        skillCandidateDto.setOffersAmount(2);
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        skills.add(skill);
        List<SkillCandidateDto> actual = listOfSkillsCandidateMapper.listToSkillCandidateDto(skills);
        List<SkillCandidateDto> expected = new ArrayList<>();
        expected.add(skillCandidateDto);
        Assertions.assertEquals(expected, actual);
    }
}
