package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

    SkillDto toSkillDTO(Skill skill);

    Skill toEntity(SkillDto skillDTO);
}
