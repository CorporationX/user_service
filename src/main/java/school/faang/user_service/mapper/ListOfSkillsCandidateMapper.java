package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListOfSkillsCandidateMapper {

    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    default List<SkillCandidateDto> listToSkillCandidateDto(List<Skill> skills) {
        Map<SkillDto, Long> dtosAndQuantities =  skills.stream().collect(Collectors.groupingBy(skillMapper::toDto, Collectors.counting()));
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();
        dtosAndQuantities.entrySet().stream().forEach(entry->skillCandidateDtos.add(new SkillCandidateDto(entry.getKey(), entry.getValue())));
        return skillCandidateDtos;
    }
}
