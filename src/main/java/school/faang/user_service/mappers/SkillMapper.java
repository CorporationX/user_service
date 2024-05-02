package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Component
@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill DtoToSkill(SkillDto skillDto);
    SkillDto skillToDto(Skill skill);

    default SkillCandidateDto skillToSkillCandidateDto(Skill skill, long offersAmount) {
        if (skill == null) {
            return null;
        }

        SkillDto skillDto = new SkillDto();
        skillDto.setId(skill.getId());
        skillDto.setTitle(skill.getTitle());

        return new SkillCandidateDto(skillDto, offersAmount);
    }
}
