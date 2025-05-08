package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring")
public interface SkillRequestMapper {

    @Mapping(source = "requestId", target = "request.id")
    @Mapping(source = "skillId", target = "skill.id")
    SkillRequest toSkillRequest(SkillRequestDto skillRequestDto);

    @InheritInverseConfiguration
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "skillId", source = "skill.id")
    SkillRequestDto toSkillRequestDto(SkillRequest skillRequest);
}
