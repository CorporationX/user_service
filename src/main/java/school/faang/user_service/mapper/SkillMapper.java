package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Map;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skillDto);

    @Mapping(source = "map2", target = "listFinal",
    qualifiedByName = "mapToDto")
    List<SkillCandidateDto> toCandidateDto(Map<Skill, Long> map);

    @Named("mapToDto")
    default List<SkillCandidateDto> mapToDto(Map<Skill, Long> map) {
        return map.entrySet().stream()
                .map(entryPair -> new SkillCandidateDto(toDto(entryPair.getKey()),
                        entryPair.getValue()))
                .toList();
    }
}
