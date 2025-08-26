package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.avro.common.SkillFilter;
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

    @Mapping(target = "guarantors", source = "guarantors")
    SkillDto toSkillDtoWithGuarantors(Skill skill, List<UserDto> guarantors);

    SkillFilter toSkillFilterDto(Skill skill);

    List<SkillFilter> toSkillFilterDtos(List<Skill> skills);
}
