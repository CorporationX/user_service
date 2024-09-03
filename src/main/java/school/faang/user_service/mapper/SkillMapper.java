package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.Skill.SkillCandidateDto;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SkillDto skillToSkillDto(Skill skill);

    @InheritInverseConfiguration
    Skill skillDtoToSkill(SkillDto skillDto);

    @Mapping(source = "id", target = "skill.id")
    @Mapping(source = "title", target = "skill.title")
    SkillCandidateDto skillToSkillCandidateDto(Skill skill);
}
