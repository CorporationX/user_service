package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {
    private Skill skill;
    private SkillDto skillDto;
    private List<User> user;
    private SkillMapperImpl skillMapper = new SkillMapperImpl();

    @BeforeEach
    void setUp() {
        user = new ArrayList<>();
        skill = Skill.builder()
                .title("test")
                .users(user)
                .build();
        skillDto = SkillDto.builder()
                .id(0L)
                .userIds(List.of())
                .title("test")
                .build();
    }

    @Test
    void testToDto() {
        SkillDto skillDto1 = skillMapper.toDto(skill);
        assertEquals(skillDto, skillDto1);
    }

    @Test
    void testToEntity() {
        Skill skill2 =  skill = Skill.builder()
                .title("test")
                .build();
        Skill skill1 = skillMapper.toEntity(skillDto);
        assertEquals(skill2, skill1);
    }
}