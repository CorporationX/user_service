package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface SkillOfferMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);
    @Mapping( source = "skillId", target = "skill.id")
    @Mapping( source = "recommendationId",target = "recommendation.id")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Named("toListOffersDto")
    List<SkillOfferDto> toListOffersDto(List<SkillOffer> skillOffers);

    @Named("toListOffersEntity")
    List<SkillOffer> toListOffersEntity(List<SkillOfferDto> skillOffersDto);
}
