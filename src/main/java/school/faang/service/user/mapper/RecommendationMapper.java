package school.faang.service.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.service.user.dto.recommendation.RecommendationDto;
import school.faang.service.user.dto.recommendation.SkillOfferDto;
import school.faang.service.user.entity.recommendation.Recommendation;
import school.faang.service.user.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Recommendation toEntity(RecommendationDto recommendationDto);

    default SkillOfferDto toSkillOfferDto(SkillOffer skillOffer) {
        return Mappers.getMapper(SkillOfferMapper.class).toDto(skillOffer);
    }

    default SkillOffer toSkillOfferEntity(SkillOfferDto skillOfferDto) {
        return Mappers.getMapper(SkillOfferMapper.class).toEntity(skillOfferDto);
    }
}
