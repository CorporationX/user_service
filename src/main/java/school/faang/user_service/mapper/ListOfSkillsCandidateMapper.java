package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListOfSkillsCandidateMapper {

    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    default List<SkillCandidateDto> listToSkillCandidateDto(List<Skill> skills) {
        List<SkillDto> skillDtos = skills.stream().map(skill -> skillMapper.toDto(skill)).toList();
        Set<SkillDto> uniqueSkills = new HashSet<>(skillDtos);
        List<SkillCandidateDto> candidateDtos = new ArrayList<>();
        uniqueSkills.forEach(skill -> {
            long amount = skillDtos.stream().filter(x -> x.equals(skill)).count();
            SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
            skillCandidateDto.setSkill(skill);
            skillCandidateDto.setOffersAmount(amount);
            candidateDtos.add(skillCandidateDto);
        });
        return candidateDtos;
    }
}
