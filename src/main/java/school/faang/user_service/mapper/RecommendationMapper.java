package school.faang.user_service.mapper;

import lombok.NoArgsConstructor;
import org.mapstruct.InjectionStrategy;
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

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
        , injectionStrategy = InjectionStrategy.CONSTRUCTOR
        , uses = SkillOfferMapper.class)

public interface RecommendationMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "skillOffersListToDto")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "skillOffersListToEntity")
    Recommendation toEntity(RecommendationDto recommendationDto);


    default SkillOfferDto skillOfferToDto (SkillOffer skillOffer) {
        return Mappers.getMapper(SkillOfferMapper.class).toDto(skillOffer);
    }

    default SkillOffer skillOfferDtoToEntity (SkillOfferDto skillOffer) {
        return Mappers.getMapper(SkillOfferMapper.class).toEntity(skillOffer);
    }

    @Named("skillOffersListToDto")
    default List<SkillOfferDto> skillOffersListToDto(List<SkillOffer> skillOffers) {
        return skillOffers.stream().map(this::skillOfferToDto).toList();
    }

    @Named("skillOffersListToEntity")
    default List<SkillOffer> skillOffersListToEntity(List<SkillOfferDto> skillOffers) {
        return skillOffers.stream().map(this::skillOfferDtoToEntity).toList();
    }

}
