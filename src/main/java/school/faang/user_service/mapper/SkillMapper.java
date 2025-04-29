package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity (SkillCreateDto skillCreateDto);
    SkillViewDto ToDto(Skill skill);
    @Mapping(target = "SkillId" , source = "skill.id")
    @Mapping(target = "offersAmount" , source = "offersAmount")

    SkillCandidateDto toSkillCandidateDto(Skill skill, int offersAmount);

    default List <SkillCandidateDto> toSkillCandidateDtoList(List <Skill> skills) {
        Map<Long,Long> skillCount = skills.stream()
                .collect(Collectors.groupingBy(Skill::getId,Collectors.counting()));

        return skills.stream()
                .distinct()
                .map(skill -> toSkillCandidateDto(skill, skillCount.get(skill.getId()).intValue()))
                .toList();

    }
}
