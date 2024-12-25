package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.model.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skillEntity);

    List<Skill> toEntityList(List<SkillDto> eventDtos);

    List<SkillDto> toDtoList(List<Skill> events);

    @Named("toSkillNameList")
    default List<String> toSkillNameList(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getTitle)
                .toList();
    }
}
