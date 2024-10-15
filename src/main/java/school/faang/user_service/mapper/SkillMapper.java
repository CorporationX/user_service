package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.SkillDto;
import school.faang.user_service.model.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toSkillDto(Skill skillEntity);

    @Mapping(target = "id", ignore = true)
    Skill toSkillEntity(SkillDto skillDto);

    List<SkillDto> toListSkillDto(List<Skill> skills);
}
