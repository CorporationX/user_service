package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SkillMapperTest {
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    public void shouldConvertEntityToDto () {
        Skill skill = Skill.builder().id(1L).title("skill").users(List.of()).build();
        SkillDto dto = skillMapper.toDto(skill);

        assertNotNull(dto);
        assertEquals(skill.getTitle(), dto.getTitle());
        assertEquals(skill.getId(), dto.getId());
    }

    @Test
    public void shouldConvertDtoToEntity () {
        SkillDto dto = SkillDto.builder().id(1L).title("skill").userIds(List.of(1L)).build();
        Skill skill = skillMapper.toEntity(dto);
        User user = User.builder().id(1L).username("David").build();
        skill.setUsers(List.of(user));

        assertNotNull(skill);
        assertEquals(dto.getId(), skill.getId());
        assertEquals(dto.getTitle(), skill.getTitle());
        assertEquals(dto.getUserIds(), skill.getUsers().stream().map(User::getId).toList());
    }

    @Test
    public void shouldConvertEntityListToDtoList () {
        Skill firstSkill = Skill.builder().id(1L).title("java").users(List.of(User.builder().username("david").id(1L).build())).build();
        Skill secondSkill = Skill.builder().id(2L).title("spring").users(List.of(User.builder().username("mark").id(2L).build())).build();
        List<Skill> skills = List.of(firstSkill, secondSkill);
        List<SkillDto> dtos = skillMapper.listToDto(skills);

        assertEquals(dtos.size(), 2);
    }
}
