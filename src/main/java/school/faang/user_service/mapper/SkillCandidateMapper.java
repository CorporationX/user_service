package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class SkillCandidateMapper {

    SkillMapper skillMapper;

    public SkillCandidateDto toDto(Skill skill, long offersAmount) {
        SkillCandidateDto skillCandidate = new SkillCandidateDto();
        skillCandidate.setSkill(skillMapper.toDto(skill));
        skillCandidate.setOffersAmount(offersAmount);
        return skillCandidate;
    }
}
