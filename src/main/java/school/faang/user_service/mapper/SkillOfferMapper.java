package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "recommendationId", target = "recommendation.id")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    List<SkillOffer> toSkillOfferList(List<SkillOfferDto> skillOfferDtoList);

    List<SkillOfferDto> toSkillOfferDtoList(List<SkillOffer> skillOfferList);
}
