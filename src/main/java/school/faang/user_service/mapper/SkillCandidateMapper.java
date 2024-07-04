package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    default SkillCandidateDto skillToSkillCandidateDto(Skill skill) {
        SkillDto skillDto = skillMapper.toDto(skill);
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        skillCandidateDto.setSkill(skillDto);
        skillCandidateDto.setOffersAmount(1L);
        return skillCandidateDto;
    }

}
