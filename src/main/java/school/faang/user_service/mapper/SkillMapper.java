package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    public SkillDto toSkillDto(Skill skill);

    public Skill toEntity(SkillDto skillDto);

    public List<SkillDto> toSkillDtoList(List<Skill> skills);

    default List<SkillCandidateDto> toSkillCandidateDtoList(List<Skill> skills) {
        return skills.stream()
                .collect(Collectors.groupingBy(this::toSkillDto, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
                    skillCandidateDto.setSkill(entry.getKey());
                    skillCandidateDto.setOffersAmount(entry.getValue());
                    return skillCandidateDto;
                }).toList();
    }
}
