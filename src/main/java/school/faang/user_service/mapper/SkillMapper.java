package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toSkill(CreateSkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    default SkillCandidateDto toSkillCandidateDto(Skill skill, int offersAmount) {
        if (skill == null) {
            return null;
        }
        SkillDto dto = toSkillDto(skill);
        return new SkillCandidateDto(dto, offersAmount);
    }

    default SkillDto toSkillDtoWithGuarantors(Skill skill, List<UserDto> guarantors) {
        if (skill == null) {
            return null;
        }
        return new SkillDto(skill.getId(), skill.getTitle(), guarantors);
    }
}
