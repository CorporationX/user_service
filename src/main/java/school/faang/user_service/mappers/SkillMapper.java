package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Component
@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);
    SkillDto toDto(Skill skill);
    @Mapping(target = "skillDto.id", source = "skill.id")
    @Mapping(target = "skillDto.title", source = "skill.title")
    SkillCandidateDto skillToSkillCandidateDto(Skill skill, long offersAmount);
}
