package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto recommendationDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Recommendation recommendationEntity(RecommendationDto recommendationDto);

    default SkillOfferDto toSkillOfferDto(SkillOffer skillOffer) {
        return Mappers.getMapper(SkillOfferMapper.class).skillOfferDto(skillOffer);
    }

    default SkillOffer toSkillOfferEntity(SkillOfferDto skillOfferDto) {
        return Mappers.getMapper(SkillOfferMapper.class).skillOfferEntity(skillOfferDto);
    }
}
