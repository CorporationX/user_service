package school.faang.user_service.mapper;

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
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RecommendationMapper {
    RecommendationMapper INSTANCE = Mappers.getMapper(RecommendationMapper.class);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOffers", source = "skillOffers", qualifiedByName = "toListOfDto")
    RecommendationDto toDto(Recommendation entity);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skillOffers", source = "skillOffers", qualifiedByName = "toListOfEntity")
    Recommendation toEntity(RecommendationDto dto);

    @Named("toListOfDto")
    default List<SkillOfferDto> toListOfDto(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOfferMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Named("toListOfEntity")
    default List<SkillOffer> toListOfEntity(List<SkillOfferDto> skillOffersDto) {
        return skillOffersDto.stream()
                .map(SkillOfferMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }

}
