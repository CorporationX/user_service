package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SkillDto skillDto);

    @Mapping(target = "offersAmount", ignore = true)
    SkillCandidateDto toCandidateDto(Skill skill);
}
