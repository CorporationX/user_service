package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class SkillCandidateMapperTest {

    @InjectMocks
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Spy
    private SkillMapperImpl skillMapper;

    @Test
    public void testMapperToDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto expected = new SkillCandidateDto();
        expected.setSkill(new SkillDto(1L, "title"));
        expected.setOffersAmount(0L);
        SkillCandidateDto actual = skillCandidateMapper.toDto(skill);
        assertThat(actual.getSkill().equals(expected.getSkill())).isTrue();
        assertThat(actual.getOffersAmount() == expected.getOffersAmount()).isTrue();
    }

    @Test
    public void testMapperListOfSkillsToCandidateDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto skillCandidateDto = skillCandidateMapper.toDto(skill);
        skillCandidateDto.setOffersAmount(2);
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        skills.add(skill);
        List<SkillCandidateDto> actual = skillCandidateMapper.toListDto(skills);
        List<SkillCandidateDto> expected = new ArrayList<>();
        expected.add(skillCandidateDto);
        Assertions.assertEquals(expected, actual);
    }

}
