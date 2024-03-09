package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDtoSkill(Skill skill);
    Skill toEntitySkill(SkillDto skillDto);
    List<SkillDto> toDtoSkill(List<Skill> skill);
   // SkillCandidateDto toSkillCandidate(Skill skill);
}
