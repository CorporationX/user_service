package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = SkillMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    SkillCandidateDto toDto(Skill skill);

    default List<SkillCandidateDto> toListDto(List<Skill> skills) {
        Map<SkillDto, Long> dtosAndQuantities =  skills.stream().collect(Collectors.groupingBy(skillMapper::toDto, Collectors.counting()));
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();
        dtosAndQuantities.forEach((key, value) -> skillCandidateDtos.add(new SkillCandidateDto(key, value)));
        return skillCandidateDtos;
    }

}
