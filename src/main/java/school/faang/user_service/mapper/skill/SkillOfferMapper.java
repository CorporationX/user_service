package school.faang.user_service.mapper.skill;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SkillOfferMapper {

    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    @Named("toSkillOfferDtos")
    default List<SkillOfferDto> toSkillOfferDtos(List<SkillOffer> skills) {
        return skills.stream()
                .map(this::toDto)
                .toList();
    }
}
