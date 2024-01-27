package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(classes = SkillMapperTest.SkillMapperTestConfig.class)
@ExtendWith(SpringExtension.class)
public class SkillMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = SkillMapperTest.class)
    public static class SkillMapperTestConfig {}

    @Autowired
    private SkillMapper skillMapper;

    @Test
    public void shouldConvertEntityToDto () {
        Skill skill = Skill.builder().id(1L).title("skill").build();
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
        Skill firstSkill = Skill.builder().id(1L).title("java").build();
        Skill secondSkill = Skill.builder().id(2L).title("spring").build();
        List<Skill> skills = List.of(firstSkill, secondSkill);
        List<SkillDto> dtos = skillMapper.listToDto(skills);

        assertEquals(dtos.size(), 2);
    }
}
