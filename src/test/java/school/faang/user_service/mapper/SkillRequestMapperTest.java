package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillRequestMapperTest {

    private final SkillRequestMapper mapper = Mappers.getMapper(SkillRequestMapper.class);
    @Test
    void testDtoToEntity() {
        var skillDto = new SkillDto(1L, "Java");
        var skillRequestDto = new SkillRequestDto(1L, new RecommendationRequest(), skillDto);

        var skillRequest = mapper.dtoToEntity(skillRequestDto);

        assertThat(skillRequest.getId()).isEqualTo(skillRequestDto.getId());
        assertThat(skillRequest.getRequest()).isNull();
        assertThat(skillRequest.getSkill().getId()).isEqualTo(skillDto.getId());
    }

    @Test
    void testEntityToDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Spring Boot");

        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setSkill(skill);

        var skillRequestDto = mapper.entityToDto(skillRequest);

        assertThat(skillRequestDto.getSkillDto().getId()).isEqualTo(skill.getId());
        assertThat(skillRequestDto.getSkillDto().getTitle()).isEqualTo(skill.getTitle());
    }

    @Test
    public void testDtoListToEntityList() {
        SkillDto skillDto1 = new SkillDto();
        skillDto1.setId(1L);
        skillDto1.setTitle("Programming");

        SkillRequestDto skillRequestDto1 = new SkillRequestDto();
        skillRequestDto1.setSkillDto(skillDto1);

        SkillDto skillDto2 = new SkillDto();
        skillDto2.setId(2L);
        skillDto2.setTitle("Testing");

        SkillRequestDto skillRequestDto2 = new SkillRequestDto();
        skillRequestDto2.setSkillDto(skillDto2);
        var skillRequestDtos = Arrays.asList(skillRequestDto1, skillRequestDto2);

        var skillRequests = mapper.dtoListToEntityList(skillRequestDtos);

        assertThat(skillRequests.size()).isEqualTo(2);
        assertThat(skillRequests.get(0).getSkill().getId()).isEqualTo(skillDto1.getId());
        assertThat(skillRequests.get(1).getSkill().getId()).isEqualTo(skillDto2.getId());
    }

    @Test
    public void testEntityListToDtoList() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Programming");

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setSkill(skill1);

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Testing");

        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest2.setSkill(skill2);
        var skillRequests = Arrays.asList(skillRequest1, skillRequest2);

        var skillRequestDtos = mapper.entityListToDtoList(skillRequests);

        assertThat(skillRequestDtos.size()).isEqualTo(2);
        assertThat(skillRequestDtos.get(0).getSkillDto().getId()).isEqualTo(skill1.getId());
        assertThat(skillRequestDtos.get(1).getSkillDto().getId()).isEqualTo(skill2.getId());
    }

}
