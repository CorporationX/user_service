package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    RecommendationMapper INSTANCE = Mappers.getMapper(RecommendationMapper.class);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toListSkillOfferDtos")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "toListSkillOffers")
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Named("toListSkillOfferDtos")
    default List<SkillOfferDto> toListSkillOfferDtos(List<SkillOffer> skillOffers){
        if (skillOffers == null){
            return null;
        }
        return skillOffers.stream().map(SkillOfferMapper.INSTANCE::toDto).toList();
    }

    @Named("toListSkillOffers")
    default List<SkillOffer> toListSkillOffers(List<SkillOfferDto> skillOfferDtos) {
        if (skillOfferDtos == null) {
            return null;
        }
        return skillOfferDtos.stream()
                .map(SkillOfferMapper.INSTANCE::toEntity)
                .toList();
    }
}
