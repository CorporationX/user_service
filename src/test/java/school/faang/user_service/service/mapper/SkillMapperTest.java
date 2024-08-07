package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class SkillMapperTest {

    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

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

}
