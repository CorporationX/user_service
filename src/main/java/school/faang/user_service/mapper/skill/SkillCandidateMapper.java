package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    @Mapping(source = "id", target = "skillDto.id")
    @Mapping(source = "title", target = "skillDto.title")
    @Mapping(target = "offersAmount", ignore = true)
    SkillCandidateDto toDto(Skill skill);

    @Mapping(source = "skillDto.id", target = "id")
    @Mapping(source = "skillDto.title", target = "title")
    Skill toEntity(SkillCandidateDto skillCandidateDto);
}