package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);
    SkillDto toDTO(Skill skill);

    Skill toEntity(SkillDto skillDTO);
    
    List<SkillDto> toListSkillsDTO(List<Skill> skills);

    List<Skill> toListSkillsEntity(List<SkillDto> skillsDto);
}
