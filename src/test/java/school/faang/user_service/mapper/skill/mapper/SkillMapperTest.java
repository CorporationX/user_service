package school.faang.user_service.mapper.skill.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {

    @InjectMocks
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillDto skillDto;
    private Skill skill;

    @BeforeEach
    void setUp() {
        skillDto = SkillDto.builder()
                .id(1L)
                .title("skill1")
                .createdAt(LocalDateTime.now())
                .build();

        skill = Skill.builder()
                .id(1L)
                .title("title")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void toSkill() {
        Skill skillFromSkillDto = skillMapper.toSkill(skillDto);
        System.out.println(skillFromSkillDto);
    }

    @Test
    void toSkillDto() {
        SkillDto skillDtoFromSkill = skillMapper.toSkillDto(skill);
        System.out.println(skillDtoFromSkill);
    }
}