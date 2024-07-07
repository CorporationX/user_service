package school.faang.user_service.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.assertj.core.api.Assertions.assertThat;

public class SkillMapperTest {

    private final SkillMapper mapper = Mappers.getMapper(SkillMapper.class);

    @Test
    public void testEntityToDto() {

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Programming");
        SkillDto skillDto = mapper.entityToDto(skill);

        assertThat(skill.getId()).isEqualTo(skillDto.getId());
        assertThat(skill.getTitle()).isEqualTo(skillDto.getTitle());
    }

    @Test
    public void testDtoToEntity() {
        SkillDto skillDto = new SkillDto(1L, "Programming");
        Skill skill = mapper.dtoToEntity(skillDto);

        assertThat(skillDto.getId()).isEqualTo(skill.getId());
        assertThat(skillDto.getTitle()).isEqualTo(skill.getTitle());
        assertThat(skill.getUsers()).isNull();
        assertThat(skill.getGuarantees()).isNull();
        assertThat(skill.getEvents()).isNull();
        assertThat(skill.getGoals()).isNull();
        assertThat(skill.getCreatedAt()).isNull();
        assertThat(skill.getUpdatedAt()).isNull();
    }
}
