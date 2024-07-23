package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toDto(Skill skill);

    Skill toEntity(SkillDto skilldto);

    default List<SkillDto> toDtoList(List<Skill> skills){
        return skills.stream()
                .map(skill -> toDto(skill))
                .collect(Collectors.toList());
    }
    default List<Skill> toEntityList(List<SkillDto> skillsdto) {
        return skillsdto.stream()
                .map(skilldto -> toEntity(skilldto))
                .collect(Collectors.toList());
    }
}

