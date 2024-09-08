package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {
    SkillCandidateDto toDto(Skill skill);
    List<SkillCandidateDto> toListDto(List<Skill> skills);
}
