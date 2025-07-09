package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;
import java.util.function.Function;

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

    default List<SkillCandidateDto> toSkillCandidateDtos(List<Skill> skills,
                                                         Function<Skill, Integer> offersAmountProvider) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(skill -> toSkillCandidateDto(skill, offersAmountProvider.apply(skill)))
                .toList();
    }

    default SkillDto toSkillDtoWithGuarantors(Skill skill, List<UserDto> guarantors) {
        if (skill == null) {
            return null;
        }
        return new SkillDto(skill.getId(), skill.getTitle(), guarantors);
    }
}
