package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toSkillDto(Skill skillEntity);

    @Mapping(target = "id", ignore = true)
    Skill toSkillEntity(SkillDto skillDto);

    List<SkillDto> toListSkillDto(List<Skill> skills);
}
