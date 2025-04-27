package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

import static school.faang.user_service.service.RecommendationService.ID_NULL_EXCEPTION;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(target = "skill", source = "skillId")
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toSkillOffer(SkillOfferDto skillOfferDto);

    @Mapping(target = "skillId", source = "skill.id")
    SkillOfferDto toSkillOfferDto(SkillOffer skillOffer);

    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtos);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);

    default Skill mapIdToSkill(Long id) {
        if (id == null) {
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }
}
