package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(source = "skillId", target = "skill.id")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    //@Named("toSkillOfferDtoList")
    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOffers);
    //@Named("toSkillOfferEntityList")
    List<SkillOffer> toSkillOfferEntityList(List<SkillOfferDto> skillOffersDto);
}
