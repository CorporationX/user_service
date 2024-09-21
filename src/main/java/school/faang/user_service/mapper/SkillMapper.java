package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);

    List<SkillDto> toDtoList(List<Skill> entityList);

    default List<SkillCandidateDto> toCandidateDtoList(List<Skill> entityList) {
        Map<Skill, Long> map = entityList.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        return map.entrySet().stream()
                .map(entry -> new SkillCandidateDto(toDto(entry.getKey()), entry.getValue()))
                .toList();
    }
}