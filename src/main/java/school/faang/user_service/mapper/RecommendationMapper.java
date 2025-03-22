package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "receiver", source = "receiverId")
    @Mapping(target = "skillOffers", source = "skillOffersDto")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recommendation toRecommendation(RecommendationDto recommendationDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffersDto", source = "skillOffers")
    RecommendationDto toRecommendationDto(Recommendation recommendation);

    List<RecommendationDto> toRecommendationDtoList(List<Recommendation> recommendations);

    @Mapping(target = "skill", source = "skillId")
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer toSkillOffer(SkillOfferDto skillOfferDto);

    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtos);

    @Mapping(target = "skillId", source = "skill.id")
    SkillOfferDto toSkillOfferDto(SkillOffer skillOffer);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);

    default User mapIdToUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }

    default Skill mapIdToSkill(Long id) {
        if (id == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }
}
