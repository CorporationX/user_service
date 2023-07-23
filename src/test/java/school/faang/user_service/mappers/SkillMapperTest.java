package school.faang.user_service.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillMapperTest {
    
    @InjectMocks
    private SkillMapper skillMapper = new SkillMapperImpl();

    @Test
    void toDto() {

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Test Skill");

        UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
        userSkillGuarantee.setId(10L);

        skill.setGuarantees(Collections.singletonList(userSkillGuarantee));


        SkillDto skillDto = skillMapper.toDto(skill);


        assertEquals(1L, skillDto.getId());
        assertEquals("Test Skill", skillDto.getTitle());
        assertEquals(1, skillDto.getGuarantees().size());
        assertEquals(10L, skillDto.getGuarantees().get(0).getId());
    }



    @Test
    void toDto_ShouldMapSkillWithGuaranteesToSkillDto() {

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Test Skill");

        UserSkillGuarantee guarantee1 = new UserSkillGuarantee();
        guarantee1.setId(100L);
        guarantee1.setSkill(skill);

        UserSkillGuarantee guarantee2 = new UserSkillGuarantee();
        guarantee2.setId(101L);
        guarantee2.setSkill(skill);

        List<UserSkillGuarantee> guarantees = Arrays.asList(guarantee1, guarantee2);
        skill.setGuarantees(guarantees);


        SkillDto skillDto = skillMapper.toDto(skill);


        assertNotNull(skillDto);
        assertEquals(1L, skillDto.getId());
        assertEquals("Test Skill", skillDto.getTitle());

        List<UserSkillGuaranteeDto> guaranteeDtos = skillDto.getGuarantees();
        assertNotNull(guaranteeDtos);
        assertEquals(2, guaranteeDtos.size());

        UserSkillGuaranteeDto guaranteeDto1 = guaranteeDtos.get(0);
        assertEquals(100L, guaranteeDto1.getId());


        UserSkillGuaranteeDto guaranteeDto2 = guaranteeDtos.get(1);
        assertEquals(101L, guaranteeDto2.getId());

    }
}