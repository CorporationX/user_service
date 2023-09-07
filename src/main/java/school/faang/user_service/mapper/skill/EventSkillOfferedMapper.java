package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventSkillOfferedMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillOfferedId", source = "skillOffers", qualifiedByName = "toSkillOfferIds")
    EventSkillOfferedDto toDto(Recommendation entity);

    @Named("toSkillOfferIds")
    default List<Long> toSkillOfferIds(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOffer::getId)
                .toList();
    }

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "receiver.id", source = "receiverId")
    @Mapping(target = "skillOffers", source = "skillOfferedId", qualifiedByName = "toSkillOffers")
    Recommendation toEntity(EventSkillOfferedDto dto);

    @Named("toSkillOffers")
    default List<SkillOffer> toSkillOffers(List<Long> ids) {
        return ids.stream()
                .map(id -> SkillOffer.builder()
                        .id(id)
                        .build())
                .toList();
    }
}