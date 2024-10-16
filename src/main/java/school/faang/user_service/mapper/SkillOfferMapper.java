package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.model.dto.SkillOfferDto;
import school.faang.user_service.model.entity.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.author.id", target = "authorId")
    @Mapping(source = "recommendation.receiver.id", target = "receiverId")
    SkillOfferDto toDto(SkillOffer skillOffer);
}

