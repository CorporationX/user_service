package school.faang.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.skill.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.title", target = "title")
    SkillRequestDto toDto(SkillRequest skillRequest);
    
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.title", target = "title")
    List<SkillRequestDto> toDto(List<SkillRequest> skillRequest);
}
