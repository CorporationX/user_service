package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.ListOfSkillsCandidateMapper;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    private final SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    private final SkillCandidateMapper skillCandidateMapper = Mappers.getMapper(SkillCandidateMapper.class);
    private final ListOfSkillsCandidateMapper listOfSkillsCandidateMapper = Mappers.getMapper(ListOfSkillsCandidateMapper.class);

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
