package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity(SkillDto dto);

    SkillDto toDto(Skill skill);
}

