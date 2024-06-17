package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface SkillCandidateMapper {
    @Mapping(source = "id", target = "skillDto.id")
    @Mapping(source = "title", target = "skillDto.title")
    @Mapping(target = "offersAmount", ignore = true)
    SkillCandidateDto toDto(Skill skill);
}
