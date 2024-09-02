package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillDto toDto(Skill skillEntity);

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SkillDto skillDto);

    List<SkillDto> toListDto(List<Skill> skills);

    SkillCandidateDto skillToSkillCandidateDto(Skill skill);
}
