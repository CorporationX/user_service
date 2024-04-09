package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {

    @Mapping(source = "skill.id", target = "skillId")
    SkillRequestDto toDto(SkillRequest skillRequest);

    @Mapping(source = "skill.id", target = "skillId")
    List<SkillRequestDto> toDtoList(List<SkillRequest> skillRequest);

    @Mapping(source = "skillId", target = "skill.id")
    List<SkillRequest> toEntity(List<SkillRequestDto> request);

    @Mapping(source = "skillId", target = "skill.id")
    List<SkillRequest> toEntityList(List<SkillRequestDto> request);
}
