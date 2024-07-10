package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {

    @Mapping(source = "requestId", target = "request.id")
    @Mapping(source = "skillId", target = "skill.id")
    SkillRequest toEntity(SkillRequestDto requestDto);

    @Mapping(source = "request.id", target = "requestId")
    @Mapping(source = "skill.id", target = "skillId")
    SkillRequestDto toDto(SkillRequest request);
}
