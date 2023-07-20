package school.faang.user_service.mappers;

import org.mapstruct.*;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skill.id", source = "skill")
    @Mapping(target = "recommendation.id", source = "recommendation")
    SkillOffer toEntity(SkillOfferDto dto);

    @Mapping(target = "skill", source ="skill.id" )
    @Mapping(target = "recommendation", source ="recommendation.id")
    SkillOfferDto toDto(SkillOffer entity);

    @Named("toSkillOfferDtos")
    default List<SkillOfferDto> toSkillOfferDtos(List<SkillOffer> skills) {
        return skills.stream()
                .map(this::toDto)
                .toList();
    }
}
