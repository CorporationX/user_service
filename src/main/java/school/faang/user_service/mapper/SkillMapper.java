package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;


@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    SkillDto skillToDto(Skill skill);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    Skill skillToEntity(SkillDto skillDto);
}
