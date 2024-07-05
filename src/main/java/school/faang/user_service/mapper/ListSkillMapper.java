package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListSkillMapper extends SkillMapper {
    List<SkillDto> listSkillToDto(List<Skill> skills);

    default SkillDto toSkillDto(Skill skill) {
        return skillToDto(skill);
    }
}
